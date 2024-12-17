package org.example.secureplatform.entity.files;

import lombok.Data;

@Data
public class DirRequest {
    // 路径
    private String path;
    // 隐藏
    private String Hidden;
    // 排序原则
    private String sortBy;
    // 排序顺序
    private String sortOrder;
    // 页数
    private int page;
    // 页数大小
    private int pageSize;
}
