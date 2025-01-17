package org.example.secureplatform.controller.docker;

import com.github.dockerjava.api.model.Network;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerNetworks;
import org.example.secureplatform.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docker")
public class NetworksController {
    @Autowired
    DockerService dockerService;
    @PostMapping("/network/search")
    public ResponseResult<List<Network>> searchNetworks(@RequestBody DockerNetworks dockerNetworks) {
        return dockerService.NetWorks(dockerNetworks.getPage(), dockerNetworks.getPageSize());
    }
    @PostMapping("/network/info")
    public ResponseResult<Network> infoNetworks(@RequestBody DockerNetworks dockerNetworks) {
        return dockerService.getNetwork(dockerNetworks);
    }
    @PostMapping("/network/remove")
    public ResponseResult removeNetwork(@RequestBody DockerNetworks dockerNetworks) {
        return dockerService.removenetwork(dockerNetworks);
    }
    @PostMapping("/network/create")
    public ResponseResult createNetwork(@RequestBody DockerNetworks dockerNetworks) {
        return dockerService.createNetwork(dockerNetworks);
    }
}
