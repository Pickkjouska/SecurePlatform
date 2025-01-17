package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.ConnectionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class DatabaseService {
    public abstract ResponseResult Connection(ConnectionRequest connectionRequest);

    public abstract ResponseResult<List<String>> getAllDatabases();

    public abstract ResponseResult<List<String>> getAllUsers();

    // 备份指定数据库的方法
    public abstract ResponseResult backupDatabase(String databaseName, String backupFilePath);

    public abstract ResponseResult importBackup(String databaseName, String backupFilePath);
}
