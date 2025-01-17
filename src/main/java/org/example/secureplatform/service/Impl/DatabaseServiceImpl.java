package org.example.secureplatform.service.Impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.ConnectionRequest;
import org.example.secureplatform.service.DatabaseService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseServiceImpl extends DatabaseService {
    private static final String DB_USERNAME = "root";  // MySQL 用户名
    private static final String DB_PASSWORD = "123456";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
  // MySQL 密码

    @Override
    public ResponseResult Connection(ConnectionRequest connectionRequest) {

        String dbUrl = connectionRequest.getUrl();
        String dbUsername = connectionRequest.getUsername();
        String dbPassword = connectionRequest.getPassword();
        DataSource dataSource = configureDataSource(dbUrl, dbUsername, dbPassword);

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return new ResponseResult(200, "Connection Successful");
            } else {
                return new ResponseResult(404, "Connection error");
            }
        } catch (SQLException e) {
            return new ResponseResult(500, e.getMessage());
        }
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
        List<String> databases = new ArrayList<>();
        String query = "SELECT schema_name FROM information_schema.schemata";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                databases.add(resultSet.getString("schema_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseResult(200, "获取成功", databases);
    }
    @Override
    public ResponseResult<List<String>> getAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT user, host, authentication_string FROM mysql.user";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String user = resultSet.getString("user");
                String host = resultSet.getString("host");
                // 将用户名和主机信息组合成一个字符串
                users.add(user + "@" + host);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 如果发生错误，返回失败的 ResponseResult
            return new ResponseResult<>(500, "获取用户信息失败: " + e.getMessage(), null);
        }
        return new ResponseResult(200, "获取成功", users);
    }
    @Override
    public ResponseResult backupDatabase(String databaseName, String backupFilePath) {
        List<String> command = new ArrayList<>();
        command.add("mysqldump");
        command.add("-h" + "localhost");
        command.add("-P" + "3306");
        command.add("-u" + DB_USERNAME);
        command.add("-p" + DB_PASSWORD);
        command.add(databaseName);
        command.add("--result-file=" + backupFilePath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return new ResponseResult(200, "数据库备份成功");
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                return new ResponseResult<>(200, "数据库备份失败", exitCode);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return new ResponseResult<>(200, "数据库备份失败", e);
        }
    }

    @Override
    public ResponseResult importBackup(String databaseName, String backupFilePath) {
        List<String> command = new ArrayList<>();
        command.add("mysql");
        command.add("-h" + "localhost");
        command.add("-P" + "3306");
        command.add("-u" + DB_USERNAME);
        command.add("-p" + DB_PASSWORD);
        command.add(databaseName);
        command.add("<");
        command.add(backupFilePath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return new ResponseResult<>(200, "数据库导入成功！", exitCode);
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                return new ResponseResult<>(200, "数据库导入失败", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseResult<>(200, "数据库备份失败", e);
        }
    }
}
