package org.example.secureplatform.common.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DockerUtil {
    private static volatile DockerClient dockerClient;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
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
            List<Image> images = listImagesCmd.withShowAll(true).exec();

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
        System.out.println("正在拉取镜像: " + name);
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(name);

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
     */
    public static Integer removeImage(String imageId) {
        Objects.requireNonNull(imageId, "镜像 ID 不能为空.");
        log.info("开始删除 Docker 镜像: {}", imageId);
        try {
            if (isRunContainer(imageId)) {
                log.warn("Docker 镜像正在被运行中的容器引用，无法删除: {}", imageId);
                return 400;
            }
            if (isReferencedByContainer(imageId)) {
                log.warn("Docker 镜像被其他容器引用，无法删除: {}", imageId);
                return 400;
            }
            dockerClient.removeImageCmd(imageId).withForce(true).exec();
            log.info("Docker 镜像删除成功: {}", imageId);
            return 200;

        } catch (ConflictException e) {
            log.error("Docker 镜像删除失败，可能被其他资源引用: {}; 错误: {}", imageId, e.getMessage());
            return 403;

        }
            catch (Exception e) {
            log.error("Docker 镜像删除失败: {}; 错误: {}", imageId, e.getMessage());
            return 500;
        }
    }
    /**
     * 删除 Docker 镜像标签
     *
     * @param repository 镜像仓库名称（如 "nginx"）
     * @param tag        镜像标签（如 "latest"）
     * @return 返回状态码，200 表示成功，400 表示失败，500 表示其他错误
     */
    public static Integer removeImageTag(String repository, String tag) {
        Objects.requireNonNull(repository, "镜像仓库名称不能为空.");
        Objects.requireNonNull(tag, "镜像标签不能为空.");
        String imageName = repository + ":" + tag;
        try {
            // 检查是否有容器正在运行基于此镜像的实例
            if (isRunContainerByTag(imageName)) {
                log.warn("Docker 镜像标签正在被运行中的容器引用，无法删除: {}", imageName);
                return 400;
            }
            // 执行删除镜像标签操作
            dockerClient.removeImageCmd(imageName).exec();
            log.info("Docker 镜像标签删除成功: {}", imageName);
            return 200;
        } catch (ConflictException e) {
            log.error("Docker 镜像标签删除失败，可能被其他资源引用: {}; 错误: {}", imageName, e.getMessage());
            return 403;

        }
        catch (Exception e) {
            log.error("Docker 镜像标签删除失败: {}; 错误: {}", imageName, e.getMessage());
            return 500;
        }
    }
    /**
     * 检查是否有运行中的容器依赖于指定的镜像标签
     *
     * @param imageName 镜像名称（如 "nginx:latest"）
     * @return 如果有运行中的容器依赖该镜像标签，则返回 true；否则返回 false
     */
    private static boolean isRunContainerByTag(String imageName) {
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();

        for (Container container : containers) {
            if (imageName.equals(container.getImage()) && "running".equalsIgnoreCase(container.getState())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 导出 Docker 镜像为文件
     * @param imageName 镜像 ID 或名称
     * @param outputPath 导出路径
     * @return 返回是否导出成功
     */
    public static boolean exportImage(String imageName, String outputPath) {
        if (imageName == null || imageName.isEmpty()) {
            throw new IllegalArgumentException("镜像 ID 不能为空.");
        }

        File outputFile = new File(outputPath);
        try (OutputStream outputStream = new FileOutputStream(outputFile);
             InputStream inputStream = dockerClient.saveImageCmd(imageName).exec()) {

            // 将镜像流写入文件
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            System.out.println("Docker 镜像导出成功: " + outputPath);
            return true;

        } catch (Exception e) {
            System.err.println("Docker 镜像导出失败: " + e.getMessage());
            return false;
        }
    }
    /**
     * 从文件导入 Docker 镜像
     * @param filePath 镜像文件路径（如 /path/to/image.tar）
     * @return 返回是否导入成功
     */
    public static boolean loadImage(String filePath) {
        File imageFile = new File(filePath);
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IllegalArgumentException("镜像文件不存在或不是有效文件: " + filePath);
        }

        try (InputStream inputStream = new FileInputStream(imageFile)) {
            LoadImageCmd loadImageCmd = dockerClient.loadImageCmd(inputStream);
            loadImageCmd.exec();
            System.out.println("Docker 镜像导入成功: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Docker 镜像导入失败: " + e.getMessage());
            return false;
        }
    }
    /**
     * 删除未使用的镜像
     */
    public static Integer removeUnusedImages() {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> images = listImagesCmd.exec();
        for (Image image : images) {
            String[] repoTags = image.getRepoTags();
            if (repoTags == null) {
                continue;
            }
            for (String imageName : repoTags) {
                if (!isRunContainer(imageName)) {
                    try {
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
     * 检查是否有任何容器（包括运行和停止的）引用该镜像
     * @param imageId 镜像 ID
     * @return true 表示有容器引用该镜像
     */
    private static boolean isReferencedByContainer(String imageId) {
        List<Container> allContainers = dockerClient.listContainersCmd()
                .withShowAll(true) // 包括所有状态的容器
                .exec();
        for (Container container : allContainers) {
            if (imageId.equals(container.getImageId())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 为镜像打标签
     *
     * @param imageName  源镜像名称或ID
     * @param repository 目标仓库名
     * @param imageTag   目标标签
     */
    public static Integer tagImage(String imageName, String repository, String imageTag) {
        try {
            // 检查源镜像是否存在
            boolean imageExists = dockerClient.listImagesCmd()
                    .exec()
                    .stream()
                    .anyMatch(image -> image.getRepoTags() != null &&
                            java.util.Arrays.asList(image.getRepoTags()).contains(imageName));

            if (!imageExists) {
                log.error("镜像不存在: {}", imageName);
                throw new NotFoundException("源镜像不存在: " + imageName);
            }
            dockerClient.tagImageCmd(imageName, repository, imageTag).exec();
            log.info("成功为镜像打标签: {} -> {}:{}", imageName, repository, imageTag);
            return 200;
        } catch (NotFoundException e) {
            log.error("无法找到指定的镜像: {}", imageName, e);
            return 403;
        } catch (Exception e) {
            log.error("为镜像打标签失败: {} -> {}:{}, 错误信息: {}", imageName, repository, imageTag, e.getMessage(), e);
            return 400;
        }
    }
    /**
     * 检查是否有运行中的容器引用该镜像
     * @param imageId 镜像 ID
     * @return true 表示有运行中的容器引用该镜像
     */
    public static boolean isRunContainer(String imageId) {
        List<Container> runningContainers = dockerClient.listContainersCmd()
                .withStatusFilter(Collections.singletonList("running")) // 仅获取运行中的容器
                .exec();
        for (Container container : runningContainers) {
            if (imageId.equals(container.getImageId())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取 Docker 容器列表
     */
    public static List<Container> listContainer() {
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
            // 获取容器列表
            List<Container> containers = listContainersCmd.withShowAll(true).exec();
            // 打印每个容器的详细信息
            if (containers != null && !containers.isEmpty()) {
                return containers;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 获取 Docker 容器具体信息
     */
    public static Container infoContainer(String containerId) {
        try {
            List<Container> containers = listContainer();
            if (containers != null) {
                for (Container container : containers) {
                    if (Objects.equals(container.getId(), containerId)){
                        return container;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    /**
     * 获取 Docker 容器日志
     *
     * @param containerId 容器 ID
     * @return 容器日志内容
     */
    public static SseEmitter getContainerLogs(String containerId, String timeFilter, int limit) {
        SseEmitter emitter = new SseEmitter();
        executor.submit(() -> {
            try {
                LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withTimestamps(true)
                        .withFollowStream(false)
                        .withTail(limit);
                if (timeFilter != null && !timeFilter.isEmpty()) {
                    LocalDateTime timeLimit = parseTimeFilter(timeFilter);
                    logContainerCmd.withSince(convertToUnixTimestamp(timeLimit));
                }
                logContainerCmd.exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            emitter.send(new String(frame.getPayload(), StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }
                    @Override
                    public void onComplete() {
                        emitter.complete();
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        emitter.completeWithError(throwable);
                    }
                }).awaitCompletion();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
    /**
     * 根据时间过滤条件解析时间
     *
     * @param timeFilter 时间过滤字符串
     * @return 过滤时间的 LocalDateTime
     */
    private static LocalDateTime parseTimeFilter(String timeFilter) {
        switch (timeFilter) {
            case "tenDay":
                return LocalDateTime.now().minusDays(10);
            case "oneDay":
                return LocalDateTime.now().minusDays(1);
            case "4hour":
                return LocalDateTime.now().minusHours(4);
            case "1hour":
                return LocalDateTime.now().minusHours(1);
            case "10min":
                return LocalDateTime.now().minusMinutes(10);
            default:
                return LocalDateTime.now().minusDays(10);  // 默认最近十天
        }
    }
    /**
     * 将 LocalDateTime 转换为 Unix 时间戳（秒）
     *
     * @param time 要转换的 LocalDateTime
     * @return Unix 时间戳（秒）
     */
    private static int convertToUnixTimestamp(LocalDateTime time) {
        return (int) time.atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
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
            portBindings.bind(tcp, Ports.Binding.bindIpAndPort("0.0.0.0", hostPort));
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
    public static void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }
    /**
     * 停止容器
     * @param containerId
     */
    public static void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }
    /**
     * 删除容器
     * @param containerId
     */
    public static void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }
    // 创建 Exec 并启用 TTY 和 STDIN
    public static ExecCreateCmdResponse createExec(String containerId, String[] command, boolean tty, boolean stdin) {
        return dockerClient.execCreateCmd(containerId)
                .withCmd(command)
                .withTty(tty)
                .withAttachStdin(stdin)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
    }
    // 连接到 Exec 输出流
    public static void attachExec(String execId, WebSocketSession session) {
        dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame frame) {
                // 将容器输出流中的内容转为字符串
                String output = new String(frame.getPayload(), StandardCharsets.UTF_8);
                try {
                    session.sendMessage(new TextMessage(output));  // 将容器输出发送到 WebSocket 客户端
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable throwable) {
                // 处理错误
                throwable.printStackTrace();
            }
            @Override
            public void onComplete() {
                // 执行完成时的清理工作
                System.out.println("Exec completed.");
            }
        });
    }

    /**
     * 获取网络信息
     *
     */
    public static List<Network> getAllNetworks() {
        ListNetworksCmd listNetworksCmd = dockerClient.listNetworksCmd();
        return listNetworksCmd.exec();
    }

    /**
     * 获取网络具体信息
     * @param networkId
     */
    public static Network getNetwork(String networkId) {
        ListNetworksCmd listNetworksCmd = dockerClient.listNetworksCmd();
        List<Network> networks = listNetworksCmd.exec();
        for (Network network : networks) {
            if (network.getId().equals(networkId)) {
                return network;  // 返回找到的网络信息
            }
        }
        return null;
    }

    /**
     * 创建网络
     *
     * @return
     * @
     */
    public static CreateNetworkResponse createNetwork(String networkName, String driver, String subnet, String gateway) {
        try {
            Network.Ipam.Config ipamConfigs = new Network.Ipam.Config()
                    .withSubnet(subnet)
                    .withGateway(gateway);
            Network.Ipam ipam = new Network.Ipam().withConfig(ipamConfigs);
            CreateNetworkCmd createNetworkCmd = dockerClient.createNetworkCmd()
                    .withName(networkName)
                    .withDriver(driver)
                    .withIpam(ipam);
            return createNetworkCmd.exec();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除网络
     */
    public static void removeNetwork(String networkId) {
        dockerClient.removeNetworkCmd(networkId).exec();
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
        System.out.println(getAllNetworks());
        System.out.println(createNetwork("my_custom_network", "bridge", "192.168.1.0/24", "192.168.1.1"));
//        createImage(imageName);
//        createAndStartContainer(imageName, containerName, 80, 8080);
//        removeUnusedImages();
    }
}
