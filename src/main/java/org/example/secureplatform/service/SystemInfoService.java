package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.osinfo.OSInfo;
import org.example.secureplatform.entity.osinfo.OSRuntimeInfo;
import org.springframework.stereotype.Service;

@Service
public abstract class SystemInfoService {
    public abstract ResponseResult<OSInfo> SystemInfo();

    public abstract ResponseResult<OSRuntimeInfo> SystemRuntime() throws InterruptedException;
}
