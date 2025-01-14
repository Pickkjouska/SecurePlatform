package org.example.secureplatform.controller.docker;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.secureplatform.service.DockerService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class DockerWebSocketHandler extends TextWebSocketHandler {
    private final DockerService dockerService;

    public DockerWebSocketHandler(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        // 解析前端发送的 JSON 数据
        String payload = message.getPayload();
        JSONObject jsonObject = JSONUtil.parseObj(payload);

        // 提取 containerId 和 command
        String containerId = jsonObject.getStr("containerId");
        String command = jsonObject.getStr("command");

        // 执行 Docker 命令并实时获取输出
        dockerService.executeAndSendLogsToFrontend(containerId, command.split(" "), session);
    }
}
