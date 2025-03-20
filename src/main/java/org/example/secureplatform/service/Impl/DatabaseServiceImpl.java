package org.example.secureplatform.service.Impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.DatabaseConnect;
import org.example.secureplatform.service.DatabaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseServiceImpl extends DatabaseService {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private DataSource configureDataSource(String dbUrl, String dbUsername, String dbPassword) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        return new HikariDataSource(config);
    }
    @Override
    public ResponseResult<List<String>> getAllDatabases() {
        return new ResponseResult(200, "获取成功", jdbcTemplate.queryForList("SHOW DATABASES", String.class));
    }
    @Override
    public ResponseResult<Map<String, List<Map<String, Object>>>> getUsers() {
        List<String> databases = jdbcTemplate.queryForList("SHOW DATABASES", String.class);
        Map<String, List<Map<String, Object>>> dbUsersMap = new HashMap<>();
        for (String db : databases) {
            String sql = "SELECT user, host FROM mysql.db WHERE db = ?";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, db);
            dbUsersMap.put(db, users);
        }
        return new ResponseResult<>(200, "获取成功", dbUsersMap);
    }

    @Override
    public ResponseResult<String> Connect(DatabaseConnect databaseConnect) {
        String url = "jdbc:mysql://" + databaseConnect.getHost() + ":" + databaseConnect.getPort() + "/" + databaseConnect.getDatabaseName();
        String username = databaseConnect.getUsername();
        String password = databaseConnect.getPassword();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Successfully connected
            return new ResponseResult<>(200, "Connection successful", "Connected to database: " + databaseConnect.getDatabaseName());
        } catch (SQLException e) {
            // Failed to connect
            return new ResponseResult<>(500, "Connection failed", "Error: " + e.getMessage());
        }
    }
}
