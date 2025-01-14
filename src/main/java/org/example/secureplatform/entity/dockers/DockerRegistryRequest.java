package org.example.secureplatform.entity.dockers;

import lombok.Data;

@Data
public class DockerRegistryRequest {
    private String name;        // 仓库名称
    private boolean auth;       // 是否需要认证
    private String username;    // 用户名
    private String password;    // 密码
    private String registryUrl; // 下载地址 (仓库地址)
    private String protocol;    // 协议 (http/https)
}
