package org.example.secureplatform.entity;

import lombok.Data;

@Data
public class DatabaseConnect {
    private String host;
    private String port;
    private String username;
    private String password;
    private String databaseName;
}
