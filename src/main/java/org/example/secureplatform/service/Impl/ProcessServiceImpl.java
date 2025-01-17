package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.ProcessInfo;
import org.example.secureplatform.service.ProcessService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessServiceImpl extends ProcessService {
    @Override
    public ResponseResult getProcesses() throws IOException {
        List<ProcessInfo> processList = new ArrayList<>();
        // 执行Linux命令，获取进程信息
        Process process = Runtime.getRuntime().exec("ps -eo pid,ppid,comm,pcpu,pmem,stat,start,user");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // 跳过第一行标题
        reader.readLine();

        // 逐行读取进程信息
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
                String threadCount = getThreadCount(processInfo.getPid());
                processInfo.setThreadCount(threadCount);
                String netstatCommand = String.format("netstat -anp | grep %d | wc -l", processInfo.getPid());
                Process netstatProcess = Runtime.getRuntime().exec(netstatCommand);
                BufferedReader netstatReader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
                String connectionCount = netstatReader.readLine();
                processInfo.setConnectionCount(Integer.parseInt(connectionCount.trim())); // 设置连接数

                processList.add(processInfo);
            }
        }
        return new ResponseResult(200, "获取成功", processList);
    }

    private String getThreadCount(int pid) throws IOException {
        String threadCommand = String.format("ps -L -p %d | wc -l", pid);
        Process threadProcess = Runtime.getRuntime().exec(threadCommand);
        BufferedReader threadReader = new BufferedReader(new InputStreamReader(threadProcess.getInputStream()));
        String threadCount = threadReader.readLine();
        return threadCount.trim();
    }

}
