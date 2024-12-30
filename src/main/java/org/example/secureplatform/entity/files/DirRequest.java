package org.example.secureplatform.entity.files;

import lombok.Data;

@Data
public class DirRequest {
    // 路径
    private String path;
    // 是否为文件夹
    private String isDir;
    // 文件内容
    private String content;
    // 隐藏
    private String Hidden;
    // 排序原则
    private String sortBy;
    // 排序顺序
    private String sortOrder;
    // 总共多少
    private String pageTotal;
    // 页数
    private int page;
    // 页数大小
    private int pageSize;
    // 旧名字
    private String oldName;
    // 新名字
    private String newName;
    // 权限
    private String permission;
    // 是否为软链接
    private String isLink;
    // 连接类型
    private String LinkType;
    // 链接地址
    private String linkPath;
}
