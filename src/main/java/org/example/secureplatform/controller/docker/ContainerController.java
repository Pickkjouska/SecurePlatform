package org.example.secureplatform.controller.docker;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerRequest;
import org.example.secureplatform.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/docker")
public class ContainerController {
    @Autowired
    DockerService dockerService;
    @PostMapping("/container/create")
    public ResponseResult createContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.createContainer(dockerRequest);
    }
    @PostMapping("/container/start")
    public ResponseResult startContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.startContainer(dockerRequest);
    }
    @PostMapping("/container/stop")
    public ResponseResult stopContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.stopContainer(dockerRequest);
    }
    @PostMapping("/container/remove")
    public ResponseResult removeContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.removeContainer(dockerRequest);
    }
    @PostMapping("/container/search")
    public ResponseResult searchContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.SearchContainer(dockerRequest.getPage(), dockerRequest.getPageSize());
    }
    @PostMapping("/container/info")
    public ResponseResult infoContainer(@RequestBody DockerRequest dockerRequest) {
        return dockerService.infoContainer(dockerRequest);
    }

    @GetMapping("/container/log")
    public SseEmitter logContainer(@RequestParam("containerId") String containerId,
                                   @RequestParam(value = "timeFilter", required = false) String timeFilter,
                                   @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return dockerService.getContainerLogs(containerId, timeFilter, limit);
    }

}
