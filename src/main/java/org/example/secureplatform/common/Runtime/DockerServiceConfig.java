//package org.example.secureplatform.common.Runtime;
//import jakarta.annotation.PostConstruct;
//import org.springframework.data.redis.core.script.ScriptExecutor;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@Service
//public class DockerServiceConfig {
//    @PostConstruct
//    public void updateDockerServiceConfig() throws IOException, InterruptedException {
//        String dockerServicePath = findDockerServicePath();
//        if (dockerServicePath == null || dockerServicePath.isEmpty()) {
//            throw new RuntimeException("无法找到 docker.service 文件路径！");
//        }
//        System.out.println("找到 docker.service 文件路径: " + dockerServicePath);
//        // 检查是否已包含 -H tcp://0.0.0.0:2375
//        if (!containsTcpOption(dockerServicePath)) {
//            String addTcpOptionCommand = "sudo sed -i '/ExecStart=/s/$/ -D --tlsverify=true --tlscert=\\/home\\/usr\\/certs\\/cert.pem --tlskey=\\/home\\/usr\\/certs\\/key.pem --tlscacert=\\/home\\/usr\\/certs\\/ca.pem -H tcp:\\/\\/0.0.0.0:2375/' " + dockerServicePath;
//            runCommand(addTcpOptionCommand);
//            System.out.println("已添加 -H tcp://0.0.0.0:2375 到 docker.service 配置中。");
//        } else {
//            System.out.println("docker.service 已包含 -H tcp://0.0.0.0:2375，无需修改。");
//        }
//
//        // 步骤 3: 重新加载 systemd 配置
//        String reloadSystemdCommand = "sudo systemctl daemon-reload";
//        runCommand(reloadSystemdCommand);
//
//        // 步骤 4: 重启 Docker 服务
//        String restartDockerCommand = "sudo systemctl restart docker";
//        runCommand(restartDockerCommand);
//
//        System.out.println("Docker 服务已成功配置并重启。");
//
//    }
//
//    // 使用 find 查找 docker.service 文件路径
//    private String findDockerServicePath() throws IOException, InterruptedException {
//        String findCommand = "find /lib/ -name docker.service 2>/dev/null";
//        System.out.println(findCommand);
//        Process process = new ProcessBuilder("bash", "-c", findCommand).start();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            return reader.readLine(); // 读取第一行输出，返回路径
//        }
//    }
//    // 执行 shell 命令
//    private void runCommand(String command) throws IOException, InterruptedException {
//        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
//        processBuilder.inheritIO(); // 将命令输出显示在控制台
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("命令执行失败: " + command);
//        }
//    }
//    // 检查 docker.service 是否已经包含 -H tcp://0.0.0.0:2375
//    private boolean containsTcpOption(String dockerServicePath) throws IOException {
//        List<String> lines = Files.readAllLines(Paths.get(dockerServicePath));
//        // 检查是否包含 -H tcp://0.0.0.0:2375
//        for (String line : lines) {
//            if (line.contains("-H tcp://0.0.0.0:2375")) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
