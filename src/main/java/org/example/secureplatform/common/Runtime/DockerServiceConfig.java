package org.example.secureplatform.common.Runtime;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DockerServiceConfig {
    @PostConstruct
    public void updateDockerServiceConfig() throws IOException, InterruptedException {
        String dockerServicePath = findDockerServicePath();
        if (dockerServicePath == null || dockerServicePath.isEmpty()) {
            throw new RuntimeException("无法找到 docker.service 文件路径！");
        }
        System.out.println("找到 docker.service 文件路径: " + dockerServicePath);
        // 检查是否已包含 -H tcp://0.0.0.0:2375
        if (!containsTcpOption(dockerServicePath)) {
            String addTcpOptionCommand = "sudo sed -i '/ExecStart=/s/$/ -H tcp:\\/\\/0.0.0.0:2375/' " + dockerServicePath;
            runCommand(addTcpOptionCommand);
            System.out.println("已添加 -H tcp://0.0.0.0:2375 到 docker.service 配置中。");
        } else {
            System.out.println("docker.service 已包含 -H tcp://0.0.0.0:2375，无需修改。");
        }
        System.out.println("daozhelilea");
        String dockerCerts = generateDockerCerts();
        System.out.println(dockerCerts);

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
        String findCommand = "find /lib/ -name docker.service 2>/dev/null";
        System.out.println(findCommand);
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
    // 检查 docker.service 是否已经包含 -H tcp://0.0.0.0:2375
    private boolean containsTcpOption(String dockerServicePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(dockerServicePath));
        // 检查是否包含 -H tcp://0.0.0.0:2375
        for (String line : lines) {
            if (line.contains("-H tcp://0.0.0.0:2375")) {
                return true;
            }
        }
        return false;
    }
    // 编写docker的TLS证书
    private String generateDockerCerts() throws IOException, InterruptedException{
        // 获取资源路径
        String scriptPath = getClass().getClassLoader().getResource("auto_gen_docker.sh").getPath();
        String tempScriptPath = System.getProperty("java.io.tmpdir") + "/auto_gen_docker.sh";
        extractScript(scriptPath, tempScriptPath);
        // tar.
        // 给脚本设置可执行权限
        try {
            Process chmodProcess = new ProcessBuilder("chmod", "a+x", tempScriptPath).start();
            chmodProcess.waitFor();
            System.out.println("Script permissions updated to executable.");

            // 执行脚本
            Process process = new ProcessBuilder("/bin/bash", tempScriptPath).start();
            process.waitFor();

            // 输出脚本执行的结果
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "编写docker的TLS证书";
    }
    // 从资源路径提取脚本到临时目录
    private static void extractScript(String resourcePath, String targetPath) throws IOException {
        InputStream inputStream = ScriptExecutor.class.getClassLoader().getResourceAsStream("auto_gen_docker.sh");
        if (inputStream == null) {
            throw new FileNotFoundException("脚本文件未找到！");
        }

        File targetFile = new File(targetPath);
        try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("脚本已提取到临时目录: " + targetPath);
    }
}
