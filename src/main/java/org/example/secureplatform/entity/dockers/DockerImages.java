package org.example.secureplatform.entity.dockers;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerImages {
    private String id;
    private String status;
    private String[] tags;
    private String size;
    private String updateTime;

    public DockerImages(Image image, List<Container> containers) {
        this.id = image.getId();
        this.status = isImageRunning(image, containers) ? "true" : "false";
        this.tags = image.getRepoTags();
        this.size = formatSize(image.getSize());
        this.updateTime = image.getCreated() != null ? formatUpdateTime(new Date(image.getCreated() * 1000L)) : "Unknown";
    }
    /**
     * 判断镜像是否正在运行（被容器使用）
     * @param image 镜像对象
     * @param containers 容器列表
     * @return true 如果该镜像被使用；false 如果未被使用
     */
    private boolean isImageRunning(Image image, List<Container> containers) {
        if (containers == null || containers.isEmpty()) {
            return false; // 没有容器，镜像一定没有被使用
        }

        String imageId = image.getId();
        for (Container container : containers) {
            if (Objects.equals(container.getImageId(), imageId)) {
                return true; // 如果有容器的镜像 ID 匹配，说明该镜像正在运行
            }
        }
        return false; // 没有匹配到，镜像未被使用
    }
    private String formatSize(Long size) {
        if (size == null || size <= 0) {
            return "Unknown";
        }
        double sizeMb = size / (1024.0 * 1024.0);
        return String.format("%.2f MB", sizeMb);
    }
    private String formatUpdateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    public static List<DockerImages> fromImageList(List<Image> images, List<Container> containers) {
        return images.stream()
                .map(image -> new DockerImages(image, containers))
                .collect(Collectors.toList());
    }
}
