package org.example.secureplatform.common.util;

import cn.hutool.core.util.ObjectUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .connectTimeout(60000)
                .readTimeout(60000)
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
    //获取docker信息
    public static Info DockerInfo() {
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
     * @param imageTag 镜像tag
     */
    public static boolean createImage(String imageName, String imageTag) throws InterruptedException, IOException {
        String name = imageName + ":" + imageTag;
        System.out.println("正在拉取镜像: " + imageName);
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imageName);

        // 自定义 PullImageResultCallback 来获取进度
        pullImageCmd.exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem  message) {
                super.onNext(message);
                // 输出拉取进度到控制台
//                System.out.println("message: " + message);
                if (Objects.equals(message.getStatus(), "Downloading")){
                    System.out.println(message.getId());
                    System.out.println("Current: " + message.getProgressDetail().getCurrent() + "Total: " + message.getProgressDetail().getTotal());
                }
                System.out.println("Status: " + message.getStatus());  // 你可以根据需要格式化输出
            }
        }).awaitCompletion();
        return true;
    }
    /**
     * 删除Docker镜像
     * @param imageId 镜像id
     * @return true表示删除成功，false表示删除失败
     */
    public static Integer removeImage(String imageId) {
        Objects.requireNonNull(imageId, "镜像 ID 不能为空.");
        log.info("开始删除 Docker 镜像: {}", imageId);
        try {
            // 如果镜像当前有容器在运行，则不进行删除操作
            if (isRunContainer(imageId)) {
                log.warn("Docker 镜像正在使用中，无法删除: {}", imageId);
                return 400;
            }
            RemoveImageCmd removeImageCmd = dockerClient.removeImageCmd(imageId);
            removeImageCmd.exec();
            log.info("Docker 镜像删除成功: {}", imageId);
            return 200;
        } catch (Exception e) {
            log.error("Docker 镜像删除失败: {};{}", imageId, e.getMessage());
            return 403;
        }
    }
    /**
     * 删除未使用的镜像
     */
    public static Integer removeUnusedImages() {
        // 获取所有镜像
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> images = listImagesCmd.exec();

        for (Image image : images) {
            String[] repoTags = image.getRepoTags();
            if (repoTags == null) {
                // 镜像可能没有标签，跳过
                continue;
            }
            for (String imageName : repoTags) {
                // 检查镜像是否有正在运行的容器使用
                if (!isRunContainer(imageName)) {
                    try {
                        // 删除未使用的镜像
                        dockerClient.removeImageCmd(imageName).exec();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 403;
                    }
                }
            }
        }
        return 200;
    }
    /**
     * 给镜像打tag
     * @param imageName 镜像名称
     * @param imageTag 镜像tag
     */
    public void tagImage(String imageName, String imageTag) {
//        dockerClient.tagImageCmd(imageName, dockerProp.getRespository(), dockerProp.getTag()).exec();
    }
    /**
     * 检查指定镜像的容器是否正在运行
     * @param imageName 镜像名称
     */
    public static boolean isRunContainer(String imageName) {
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        // 获取正在运行的容器（包括容器的镜像信息）
        List<Container> containers = listContainersCmd.exec();
        // 遍历容器列表，检查镜像名称
        for (Container container : containers) {
            // 检查容器的镜像是否匹配
            if (container.getImage().equals(imageName)) {
                System.out.println("容器正在运行: " + imageName);
                return true; // 找到正在运行的容器
            }
        }
        System.out.println("容器未运行: " + imageName);
        return false; // 没有找到正在运行的容器
    }
    /**
     * 创建并启动 Docker 容器
     * @param imageName 镜像名称
     * @param imageTag 镜像tag
     * @param containerName 容器名称
     * @param hostPort 主机端口
     * @param containerPort 容器端口
     */
    public static String createContainer(String imageName, String imageTag, String containerName, int hostPort, int containerPort) {
        try {
            String name = imageName + ":" + imageTag;
            ExposedPort tcp = ExposedPort.tcp(containerPort);
            // 创建容器
            Ports portBindings = new Ports();
            portBindings.bind(tcp, Ports.Binding.bindPort(hostPort));
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd(name)
                    .withName(containerName) // 设置容器名称
                    .withHostConfig(HostConfig.newHostConfig()
                    .withPortBindings(portBindings)) // 映射端口
                    .exec();

            // 启动容器
            String containerId = containerResponse.getId();
            dockerClient.startContainerCmd(containerId).exec();
            System.out.println("容器已创建并启动，容器ID：" + containerId);
            return containerId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 启动容器
     * @param containerId
     */
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }
    /**
     * 停止容器
     * @param containerId
     */
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }
    /**
     * 删除容器
     * @param containerId
     */
    public void removeContainer(DockerClient client, String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
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
                .withDockerHost("tcp://192.168.218.139:2375")
                //API版本 可通过在服务器 docker version 命令查看
                .withDockerApiVersion("1.47")
                //安全连接密钥文件存放路径
//                .withDockerCertPath("/home/usr/certs/")
                .build();
        String imageName = "nginx:latest";  // 使用 nginx 镜像
        String containerName = "my-nginx-container";
//        createImage(imageName);
//        createAndStartContainer(imageName, containerName, 80, 8080);
//        removeUnusedImages();
        System.out.println(listImages());

    }
}
