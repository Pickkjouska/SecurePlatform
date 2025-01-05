package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.dockers.DockerImages;
import org.example.secureplatform.entity.dockers.DockerRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public abstract class DockerService {
    public abstract ResponseResult<List<DockerImages>> SearchImages(Integer page, Integer pageSize);
    public abstract ResponseResult CreateImages(DockerRequest dockerRequest) throws IOException, InterruptedException;

    public abstract ResponseResult removeImage(DockerRequest dockerRequest) throws IOException, InterruptedException;

    public abstract ResponseResult removeUnusedImages();

    public abstract ResponseResult createContainer(DockerRequest dockerRequest);
}
