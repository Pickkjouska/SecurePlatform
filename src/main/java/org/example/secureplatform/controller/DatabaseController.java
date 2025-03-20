package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.DatabaseConnect;
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
    @GetMapping("/database")
    public ResponseResult getDatabase() {
        return databaseService.getAllDatabases();
    }
    @GetMapping("/users")
    public ResponseResult<Map<String, List<Map<String, Object>>>> getMysqlUsers() {
        return databaseService.getUsers();
    }
    @PostMapping("/connect")
    public ResponseResult connect(@RequestBody DatabaseConnect databaseConnect) {
        return databaseService.Connect(databaseConnect);
    }
}
