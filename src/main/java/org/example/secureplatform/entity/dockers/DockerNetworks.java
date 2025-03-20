package org.example.secureplatform.entity.dockers;

import lombok.Data;

@Data
public class DockerNetworks {
    private String Id;
    private String networkId;
    private String Name;
    private String driver;
    private String subnet;
    private String gateway;
    private int page;
    private int pageSize;
}
