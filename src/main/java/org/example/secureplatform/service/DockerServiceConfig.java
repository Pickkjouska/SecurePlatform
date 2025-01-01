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
        String dockerServicePath = "/lib/systemd/system/docker.service";
    }
    // 使用 find 查找 docker.service 文件路径
    private String findDockerServicePath() throws IOException, InterruptedException {
        String findCommand = "find / -name docker.service 2>/dev/null";
        Process process = new ProcessBuilder("bash", "-c", findCommand).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine(); // 读取第一行输出，返回路径
        }
    }
}
