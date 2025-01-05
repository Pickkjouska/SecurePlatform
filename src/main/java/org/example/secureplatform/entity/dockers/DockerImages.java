package org.example.secureplatform.entity.dockers;

import com.github.dockerjava.api.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerImages {
    private String id;
    private String status;
    private String tags;
    private String size;
    private String updateTime;

    public DockerImages(Image image) {
        this.id = image.getId();
        this.status = image.getParentId() != null && !image.getParentId().isEmpty() ? "true" : "false";
        this.tags = image.getRepoTags() != null ? String.join(", ", image.getRepoTags()) : "No Tags";
        this.size = formatSize(image.getSize());
        this.updateTime = image.getCreated() != null ? new java.util.Date(image.getCreated() * 1000L).toString() : "Unknown";
    }

    private String formatSize(Long size) {
        if (size == null || size <= 0) {
            return "Unknown";
        }
        double sizeMb = size / (1024.0 * 1024.0);
        return String.format("%.2f MB", sizeMb);
    }

    public static List<DockerImages> fromImageList(List<Image> images) {
        return images.stream()
                .map(DockerImages::new)
                .collect(Collectors.toList());
    }
}
