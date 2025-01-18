package org.example.secureplatform.entity;

import lombok.Data;

@Data
public class ProcessInfo {
    private int pid;
    private int ppid;
    private String name;
    private String cpuUsage;
    private String memoryUsage;
    private String status;
    private String startTime;
    private String user;

}
