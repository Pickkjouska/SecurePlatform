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
@RequestMapping("/docker/images")
public class ImagesController {
    @Autowired
    DockerService dockerService;
    @PostMapping("/search")
    public ResponseResult<List<DockerImages>> searchImages(@RequestBody DockerRequest dockerRequest) {
        return dockerService.SearchImages(dockerRequest.getPage(), dockerRequest.getPageSize());
    }
    @PostMapping("/info")
    public ResponseResult infoImage() {
        return dockerService.infoImages();
    }
    @PostMapping("/create")
    public ResponseResult createImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        System.out.println(dockerRequest.getImageName());
        return dockerService.CreateImages(dockerRequest);
    }
    @PostMapping("/remove")
    public ResponseResult removeImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.removeImage(dockerRequest);
    }
    @PostMapping("/removeTag")
    public ResponseResult removeTag(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.removeImageTag(dockerRequest);
    }
    @PostMapping("/removeUnused")
    public ResponseResult removeUnusedImages() throws IOException, InterruptedException {
        return dockerService.removeUnusedImages();
    }
    @PostMapping("/tagImage")
    public ResponseResult tagImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.tagImage(dockerRequest);
    }
    @PostMapping("/exportImage")
    public ResponseResult exportImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.exportImage(dockerRequest);
    }
    @PostMapping("/loadImage")
    public ResponseResult loadImage(@RequestBody DockerRequest dockerRequest) throws IOException, InterruptedException {
        return dockerService.loadImage(dockerRequest);
    }
}
