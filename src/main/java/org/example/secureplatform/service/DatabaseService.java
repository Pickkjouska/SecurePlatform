package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.DatabaseConnect;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class DatabaseService {

    public abstract ResponseResult<List<String>> getAllDatabases();

    public abstract ResponseResult<Map<String, List<Map<String, Object>>>> getUsers();

    public abstract ResponseResult<String> Connect(DatabaseConnect databaseConnect);
}
