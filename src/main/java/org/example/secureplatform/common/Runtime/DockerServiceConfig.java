package org.example.secureplatform.common.Runtime;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DockerServiceConfig {
    @PostConstruct
    public void updateDockerServiceConfig() throws IOException, InterruptedException {
        String dockerServicePath = findDockerServicePath();
        if (dockerServicePath == null || dockerServicePath.isEmpty()) {
            throw new RuntimeException("无法找到 docker.service 文件路径！");
        }
        System.out.println("找到 docker.service 文件路径: " + dockerServicePath);

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
    // 编写docker的TLS证书
    private void generateDockerCerts() {
        // 获取资源路径
        String scriptPath = getClass().getClassLoader().getResource("auto_gen_docker.sh").getPath();
        // 设定解压路径
        String targetPath = getClass().getClassLoader().getResource("certs").getPath();
        // 给脚本设置可执行权限
        try {
            Process chmodProcess = new ProcessBuilder("chmod", "+x", scriptPath).start();
            chmodProcess.waitFor();
            System.out.println("Script permissions updated to executable.");

            // 执行脚本
            Process process = new ProcessBuilder("/bin/bash", scriptPath).start();
            process.waitFor();

            // 输出脚本执行的结果
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // 解压生成的证书文件到本地 resources 目录
            extractCerts(targetPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void extractCerts(String targetPath) {
        // 解压目标 tar.gz 文件到 resources 下
        File tarFile = new File(targetPath, "tls-client-certs-docker.tar.gz");
        if (tarFile.exists()) {
            try {
                // 创建解压目录
                Path targetDir = new File(targetPath).toPath();
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                // 解压文件
                Process unzipProcess = new ProcessBuilder("tar", "-xzvf", tarFile.getAbsolutePath(), "-C", targetPath).start();
                unzipProcess.waitFor();

                System.out.println("解压至: " + targetPath);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("文件未找到: " + tarFile.getAbsolutePath());
        }
    }
}
