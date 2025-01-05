package org.example.secureplatform.controller.docker;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerImages;
import org.example.secureplatform.entity.dockers.DockerRequest;
import org.example.secureplatform.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/docker")
public class ImagesController {
    @Autowired
    DockerService dockerService;
    @PostMapping("/images/search")
    public ResponseResult<List<DockerImages>> searchImages(@RequestBody DockerRequest dockerRequest) {
        return dockerService.SearchImages(dockerRequest.getPage(), dockerRequest.getPageSize());
    }
    @PostMapping("/images/create")
    public ResponseResult createImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        System.out.println(dockerRequest.getImageName());
        return dockerService.CreateImages(dockerRequest);
    }
    @PostMapping("/images/remove")
    public ResponseResult removeImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.removeImage(dockerRequest);
    }
    @GetMapping("images/removeUnused")
    public ResponseResult removeUnusedImages() throws IOException, InterruptedException {
        return dockerService.removeUnusedImages();
    }
}
