package org.example.secureplatform.entity.osinfo;

import lombok.Data;

//  磁盘信息
@Data
public class DisksInfo {
    // 文件系统的挂载点
    private String dirName;

    // 文件系统名称
    private String sysTypeName;

    // 文件系统的类型
    private String typeName;

    // 总大小
    private long total;
    private String ConTotal;
    // 剩余大小
    private long free;
    private String ConFree;
    // 已使用量
    private long used;
    private String ConUsed;
    // 资源的使用率
    private double usage = 100;
}
