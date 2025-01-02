package org.example.secureplatform.common.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
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
                //如果开启安全连接，需要配置这行
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
    public Info DockerInfo () {
        return dockerClient.infoCmd().exec();
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
    public static void main(String[] args) throws InterruptedException {
//        DockerUtil dockerUtil = new DockerUtil
//                .Builder()
//                //服务器ip
//                .withDockerHost("tcp://192.168.218.131:2375")
//                //API版本 可通过在服务器 docker version 命令查看
//                .withDockerApiVersion("1.41")
//                //安全连接密钥文件存放路径
//                .withDockerCertPath("/home/user/certs/")
//                .build();
//        System.out.println("docker的环境信息:========" + dockerUtil.DockerInfo());
        String scriptPath = DockerUtil.class.getClassLoader().getResource("auto_gen_docker.sh").getPath();
        System.out.println(scriptPath);
    }
}
