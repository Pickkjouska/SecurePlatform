package org.example.secureplatform.entity.files;


import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
public class DirInfo {
    // 文件夹路径
    private String path;
    // 文件夹名
    private String name;
    // 用户
    private String user;
    // 组
    private String group;
    // 文本
    private String content;
    // 文本类型
    private String mimeType;
    // 大小
    private String size;
    // 是否文件夹
    private String isDir;
    // 符号链接
    private String isSymlink;
    // 隐藏文件
    private String isHidden;
    // 链接路径
    private Path linkPath;
    // 权限等级
    private String mode;
    // 更新时间
    private String updateTime;
    // 下一级目录
    private List<DirInfo> item;
    // item总共
    private int itemTotal;

}
