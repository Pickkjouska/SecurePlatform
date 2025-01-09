package org.example.secureplatform.controller.docker;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerRequest;
import org.example.secureplatform.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
