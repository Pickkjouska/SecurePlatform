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

    public abstract ResponseResult infoImages();

    public abstract ResponseResult CreateImages(DockerRequest dockerRequest) throws IOException, InterruptedException;

    public abstract ResponseResult removeImage(DockerRequest dockerRequest) throws IOException, InterruptedException;

    public abstract ResponseResult removeImageTag(DockerRequest dockerRequest);

    public abstract ResponseResult exportImage(DockerRequest dockerRequest);

    public abstract ResponseResult removeUnusedImages();

    public abstract ResponseResult tagImage(DockerRequest dockerRequest);

    public abstract ResponseResult createContainer(DockerRequest dockerRequest);

    public abstract ResponseResult startContainer(DockerRequest dockerRequest);

    public abstract ResponseResult stopContainer(DockerRequest dockerRequest);

    public abstract ResponseResult removeContainer(DockerRequest dockerRequest);

    public abstract ResponseResult SearchContainer(Integer page, Integer pageSize);

    public abstract ResponseResult infoContainer(DockerRequest dockerRequest);
}
