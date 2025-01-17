package org.example.secureplatform.entity;

import lombok.Data;

@Data
public class ConnectionRequest {
    private String url;
    private String username;
    private String password;
    private String databaseName;
    private String backupFilePath;
}
