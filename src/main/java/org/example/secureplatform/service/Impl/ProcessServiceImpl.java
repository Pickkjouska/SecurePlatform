package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.NetworkConnection;
import org.example.secureplatform.entity.ProcessInfo;
import org.example.secureplatform.service.ProcessService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProcessServiceImpl extends ProcessService {
    @Override
    public ResponseResult getProcesses() throws IOException {
        List<ProcessInfo> processList = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("ps -eo pid,ppid,comm,pcpu,pmem,stat,start,user");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] processDetails = line.trim().split("\\s+");
            if (processDetails.length >= 7) {
                ProcessInfo processInfo = new ProcessInfo();
                processInfo.setPid(Integer.parseInt(processDetails[0]));
                processInfo.setPpid(Integer.parseInt(processDetails[1]));
                processInfo.setName(processDetails[2]);
                processInfo.setCpuUsage(processDetails[3]);
                processInfo.setMemoryUsage(processDetails[4]);
                processInfo.setStatus(processDetails[5]);
                processInfo.setStartTime(processDetails[6]);
                processInfo.setUser(processDetails[7]);
                processList.add(processInfo);
            }
        }
        return new ResponseResult(200, "获取成功", processList);
    }

    @Override
    public ResponseResult delProcesses(int pid) throws IOException {
        try {
            // 执行 kill 命令来终止指定的进程
            String command = String.format("kill -9 %d", pid);
            Process process = Runtime.getRuntime().exec(command);

            // 等待命令执行完毕
            int exitCode = process.waitFor();

            // 判断命令执行是否成功
            if (exitCode == 0) {
                return new ResponseResult(200, "successful");
            } else {
                return new ResponseResult(200,"Failed to terminate process " + pid + ".");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseResult(200, e.getMessage());
        }
    }

    @Override
    public ResponseResult getNetworkConnections() throws IOException {
        List<NetworkConnection> connections = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("ss -tulnp");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] details = line.trim().split("\\s+");
            System.out.println(Arrays.toString(details));
            // 如果输出的行格式正确且字段数大于等于7
            if (details.length >= 7) {
                try {
                    NetworkConnection connection = new NetworkConnection();
                    connection.setProto(details[0]);
                    connection.setState(details[1]);
                    connection.setLocalAddress(details[4]);
                    connection.setPeerAddress(details[5]);
                    String processInfo = details[6];
                    Pattern pattern = Pattern.compile("\"([^\"]+)\".*pid=(\\d+)");
                    Matcher matcher = pattern.matcher(processInfo);
                    if (matcher.find()) {
                        String processName = matcher.group(1);
                        String pid = matcher.group(2);
                        connection.setPid(pid);
                        connection.setProcessName(processName);
                    } else {
                        if (processInfo.contains("/")) {
                            connection.setPid(processInfo.split("/")[0]); // 进程ID
                            connection.setProcessName(processInfo.split("/")[1]); // 进程名称
                        } else {
                            connection.setPid("");
                            connection.setProcessName(processInfo);
                        }
                    }
                    connections.add(connection);
                } catch (Exception e) {
                    System.err.println(e + line);
                    continue;
                }
            } else {
                System.err.println(details.length + line);
            }
        }
        return new ResponseResult(200, "获取成功", connections);
    }
}
