package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.ConnectionRequest;
import org.example.secureplatform.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/database")
public class DatabaseController {
    @Autowired
    private DatabaseService databaseService;
    @PostMapping("/connection")
    public ResponseResult connection(@RequestBody ConnectionRequest connectionRequest) {
        return databaseService.Connection(connectionRequest);
    }
    @GetMapping("/database")
    public ResponseResult getDatabase() {
        return databaseService.getAllDatabases();
    }
    @GetMapping("/user")
    public ResponseResult getUser() {
        return databaseService.getAllUsers();
    }
    @PostMapping("/backup")
    public ResponseResult backup(@RequestBody ConnectionRequest connectionRequest) {
        return databaseService.backupDatabase(connectionRequest.getDatabaseName(), connectionRequest.getBackupFilePath());
    }
}
