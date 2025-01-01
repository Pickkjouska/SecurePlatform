package org.example.secureplatform.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class DockerServiceConfig {
    @PostConstruct
    public void upadteDockerServiceConfig() throws IOException, InterruptedException {
        // 步骤 1: 查找 docker.service 文件路径
        String dockerServicePath = findDockerServicePath();
        if (dockerServicePath == null || dockerServicePath.isEmpty()) {
            throw new RuntimeException("无法找到 docker.service 文件路径！");
        }
        System.out.println("找到 docker.service 文件路径: " + dockerServicePath);

        // 步骤 2: 修改 docker.service 文件，添加 -H tcp://0.0.0.0:2375
        String addTcpOptionCommand = "sudo sed -i '/ExecStart=/s/$/ -H tcp:\\/\\/0.0.0.0:2375/' " + dockerServicePath;
        runCommand(addTcpOptionCommand);

        // 步骤 3: 重新加载 systemd 配置
        String reloadSystemdCommand = "sudo systemctl daemon-reload";
        runCommand(reloadSystemdCommand);

        // 步骤 4: 重启 Docker 服务
        String restartDockerCommand = "sudo systemctl restart docker";
        runCommand(restartDockerCommand);

        System.out.println("Docker 服务已成功配置并重启。");

    }

    // 使用 find 查找 docker.service 文件路径
    private String findDockerServicePath() throws IOException, InterruptedException {
        String findCommand = "find / -name docker.service 2>/dev/null";
        Process process = new ProcessBuilder("bash", "-c", findCommand).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine(); // 读取第一行输出，返回路径
        }
    }
    // 执行 shell 命令
    private void runCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.inheritIO(); // 将命令输出显示在控制台
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("命令执行失败: " + command);
        }
    }
}
