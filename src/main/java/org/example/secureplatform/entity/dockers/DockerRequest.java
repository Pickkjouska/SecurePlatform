package org.example.secureplatform.entity.dockers;

import lombok.Data;

@Data
public class DockerRequest {
    private String imageId;
    private String imageName;
    private String imageTag;
    private String containerName;
    private String repository;
    private String containerId;
    private String path;
    private int hostPort;
    private int containerPort;
    private int page;
    private int pageSize;
}
