package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public abstract class ProcessService {
    public abstract ResponseResult getProcesses() throws IOException;

    public abstract ResponseResult delProcesses(int pid) throws IOException;

    public abstract ResponseResult getNetworkConnections() throws IOException;
}
