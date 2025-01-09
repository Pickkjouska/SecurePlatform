package org.example.secureplatform.service.Impl;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.common.util.DockerUtil;
import org.example.secureplatform.entity.dockers.DockerImages;
import org.example.secureplatform.entity.dockers.DockerRequest;
import org.example.secureplatform.service.DockerService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DockerServiceImpl extends DockerService {
    DockerUtil dockerUtil = new DockerUtil
            .Builder()
            //服务器ip
            .withDockerHost("tcp://192.168.218.139:2375")
            //API版本 可通过在服务器 docker version 命令查看
            .withDockerApiVersion("1.47")
            //安全连接密钥文件存放路径
//                .withDockerCertPath("/home/usr/certs/")
            .build();

    @Override
    public ResponseResult<List<DockerImages>> SearchImages(Integer page, Integer pageSize) {
        List<Image> images = DockerUtil.listImages();
        List<Container> containers = DockerUtil.listContainer();
        System.out.println(images);
        // 根据 page 和 pageSize 做分页处理
        if (images != null && !images.isEmpty()) {
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, images.size());
            // 返回分页后的镜像列表
            List<Image> paginatedImages = images.subList(start, end);
            return new ResponseResult<List<DockerImages>>(200, "获取成功", DockerImages.fromImageList(paginatedImages, containers));
        }else{
            return new ResponseResult<List<DockerImages>>(200, "获取成功，无数据");
        }
    }

    @Override
    public ResponseResult infoImages () {
        List<Image> images = DockerUtil.listImages();
        return new ResponseResult<>(200, "获取成功", images);
    }

    @Override
    public ResponseResult CreateImages(DockerRequest dockerRequest) throws IOException, InterruptedException {
        if (dockerRequest.getImageTag() == null) {
            dockerRequest.setImageTag("latest");
        }
        if (DockerUtil.createImage(dockerRequest.getImageName(), dockerRequest.getImageTag())){
            return new ResponseResult<>(200, "创建成功");
        }else{
            return new ResponseResult<>(404, "创建失败");
        }
    }
    @Override
    public ResponseResult removeImage(DockerRequest dockerRequest) {
        if (DockerUtil.removeImage(dockerRequest.getImageId()) == 200){
            return new ResponseResult<>(200, "删除成功");
        } else if (DockerUtil.removeImage(dockerRequest.getImageId()) == 400) {
            return new ResponseResult<>(400, "镜像正在使用");
        } else {
            return new ResponseResult<>(404, "删除失败");
        }
    }
    @Override
    public ResponseResult removeImageTag(DockerRequest dockerRequest) {
        if (DockerUtil.removeImageTag(dockerRequest.getRepository(), dockerRequest.getImageTag()) == 200){
            return new ResponseResult<>(200, "标签删除成功");
        } else if (DockerUtil.removeImageTag(dockerRequest.getRepository(), dockerRequest.getImageTag()) == 400) {
            return new ResponseResult<>(400, "镜像正在使用");
        } else {
            return new ResponseResult<>(404, "删除失败");
        }
    }
    @Override
    public ResponseResult exportImage(DockerRequest dockerRequest) {
        if (DockerUtil.exportImage(dockerRequest.getImageTag(), dockerRequest.getPath())){
            return new ResponseResult<>(200, "导出成功");
        }else {
            return new ResponseResult<>(400, "导出失败");
        }
    }
    @Override
    public ResponseResult removeUnusedImages(){
        if (DockerUtil.removeUnusedImages() == 200){
            return new ResponseResult<>(200, "已删除未使用镜像");
        } else if (DockerUtil.removeUnusedImages() == 403) {
            return new ResponseResult<>(403, "删除失败");
        } else {
            return new ResponseResult<>(403, "删除失败");
        }
    }
    @Override
    public ResponseResult tagImage(DockerRequest dockerRequest){
        String imageName = dockerRequest.getImageName();
        String imageTag = dockerRequest.getImageTag();
        String repository = dockerRequest.getRepository();
        if (DockerUtil.tagImage(imageName, repository, imageTag) == 200){
            return new ResponseResult<>(200, "成功为镜像打标签");
        } else if (DockerUtil.tagImage(imageName, repository, imageTag) == 400) {
            return new ResponseResult<>(200, "为镜像打标签失败");
        } else {
            return new ResponseResult<>(200, "无法找到指定的镜像");
        }
    }

    // Container

    @Override
    public ResponseResult createContainer(DockerRequest dockerRequest) {
        String imageName = dockerRequest.getImageName();
        String imageTag = dockerRequest.getImageTag();
        String containerName = dockerRequest.getContainerName();
        int hostPort = dockerRequest.getHostPort();
        int containerPort = dockerRequest.getContainerPort();
        String containerId = DockerUtil.createContainer(imageName, imageTag, containerName, hostPort, containerPort);
        if (containerId != null){
            return new ResponseResult<>(200, "创建成功", containerId);
        }else{
            return new ResponseResult<>(403, "创建失败");
        }
    }

    @Override
    public ResponseResult startContainer(DockerRequest dockerRequest) {
        String containerId = dockerRequest.getContainerId();
        DockerUtil.startContainer(containerId);
        return new ResponseResult<>(200, "启动成功", containerId);
    }

    @Override
    public ResponseResult stopContainer(DockerRequest dockerRequest) {
        String containerId = dockerRequest.getContainerId();
        DockerUtil.stopContainer(containerId);
        return new ResponseResult<>(200, "已停止", containerId);
    }

    @Override
    public ResponseResult removeContainer(DockerRequest dockerRequest) {
        String containerId = dockerRequest.getContainerId();
        DockerUtil.removeContainer(containerId);
        return new ResponseResult<>(200, "成功删除", containerId);
    }

    @Override
    public ResponseResult SearchContainer (Integer page, Integer pageSize){
        List<Container> containers = DockerUtil.listContainer();
        if (containers != null && !containers.isEmpty()) {
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, containers.size());
            // 返回分页后的镜像列表
            List<Container> paginatedContainer = containers.subList(start, end);
            return new ResponseResult<>(200, "获取成功", paginatedContainer);
        }else{
            return new ResponseResult<>(200, "获取成功，无数据");
        }
    }

    @Override
    public ResponseResult infoContainer (DockerRequest dockerRequest) {
        String containerId = dockerRequest.getContainerId();
        Container container = DockerUtil.infoContainer(containerId);
        return new ResponseResult<>(200, "获取成功", container);
    }
}
