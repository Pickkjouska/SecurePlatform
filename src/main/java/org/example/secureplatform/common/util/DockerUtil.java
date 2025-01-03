package org.example.secureplatform.common.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.example.secureplatform.common.ResponseResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DockerUtil {
    private static volatile DockerClient dockerClient;

    private DockerUtil() {}

    private DockerUtil(String dockerHost, String dockerApiVersion, String dockerCertPath) {
        Objects.requireNonNull(dockerHost, "Docker 主机地址不能为空.");
        Objects.requireNonNull(dockerApiVersion, "Docker API 版本不能为空.");

        // 使用双重校验锁实现 Docker 客户端单例
        if (dockerClient == null) {
            synchronized (DockerUtil.class) {
                if (dockerClient == null) {
                    dockerClient = createDockerClient(dockerHost, dockerApiVersion, dockerCertPath);
                }
            }
        }
    }
    private DockerClient createDockerClient(String dockerHost, String dockerApiVersion, String dockerCertPath) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withApiVersion(dockerApiVersion)
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(false)
//                .withDockerCertPath(dockerCertPath)
                .build();

        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectTimeout(60 * 60)
                .readTimeout(60 * 5)
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
    //获取docker信息
    public Info DockerInfo () {
        return dockerClient.infoCmd().exec();
    }
    /**
     * 获取并打印 Docker 镜像列表
     */
    public static List<Image> listImages() {
        try {
            ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
            // 获取镜像列表
            List<Image> images = listImagesCmd.exec();

            // 打印每个镜像的详细信息
            if (images != null && !images.isEmpty()) {
                return images;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建 Docker 镜像
     * @param imageName 镜像名称
     */
    public static void createImage(String imageName) throws InterruptedException, IOException {

        System.out.println("正在拉取镜像: " + imageName);
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        System.out.println("镜像 " + imageName + " 已拉取成功！");
    }
    /**
     * 创建并启动 Docker 容器
     * @param imageName 镜像名称
     * @param containerName 容器名称
     * @param hostPort 主机端口
     * @param containerPort 容器端口
     */
    public static void createAndStartContainer(String imageName, String containerName, int hostPort, int containerPort) {
        try {
            // 将端口转换为 String 类型
            ExposedPort tcp = ExposedPort.tcp(hostPort);
            // 创建容器
            Ports portBindings = new Ports();
            portBindings.bind(tcp, Ports.Binding.bindPort(containerPort));
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageName)
                    .withName(containerName) // 设置容器名称
                    .withHostConfig(HostConfig.newHostConfig()
                            .withPortBindings(portBindings)) // 映射端口
                    .exec();

            // 启动容器
            String containerId = containerResponse.getId();
            dockerClient.startContainerCmd(containerId).exec();

            System.out.println("容器已创建并启动，容器ID：" + containerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private String dockerHost;
        private String dockerApiVersion;
        private String dockerCertPath;

        public Builder withDockerHost(String dockerHost) {
            this.dockerHost = dockerHost;
            return this;
        }

        public Builder withDockerApiVersion(String dockerApiVersion) {
            this.dockerApiVersion = dockerApiVersion;
            return this;
        }

        public Builder withDockerCertPath(String dockerCertPath) {
            this.dockerCertPath = dockerCertPath;
            return this;
        }

        public DockerUtil build() {
            return new DockerUtil(dockerHost, dockerApiVersion, dockerCertPath);
        }
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        DockerUtil dockerUtil = new DockerUtil
                .Builder()
                //服务器ip
                .withDockerHost("tcp://192.168.218.131:2375")
                //API版本 可通过在服务器 docker version 命令查看
                .withDockerApiVersion("1.41")
                //安全连接密钥文件存放路径
//                .withDockerCertPath("/home/usr/certs/")
                .build();
        String imageName = "nginx:latest";  // 使用 nginx 镜像
        String containerName = "my-nginx-container";
        createImage(imageName);
//        createAndStartContainer(imageName, containerName, 80, 8080);
        System.out.println(listImages());

    }
}
