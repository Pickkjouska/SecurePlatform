package org.example.secureplatform.entity;

import lombok.Data;

@Data
public class NetworkConnection {
    private String proto;
    private String state;
    private String localAddress;
    private String peerAddress;
    private String pid;
    private String processName;
}
