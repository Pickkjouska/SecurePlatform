package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.common.util.SystemInfoUtil;
import org.example.secureplatform.entity.osinfo.OSInfo;
import org.example.secureplatform.entity.osinfo.OSRuntimeInfo;
import org.example.secureplatform.service.SystemInfoService;
import org.springframework.stereotype.Service;

@Service
public class SystemInfoServiceImpl extends SystemInfoService {

    @Override
    public ResponseResult<OSInfo> SystemInfo() {
        return new ResponseResult<>(200, "获取成功", SystemInfoUtil.getSystemInfo());
    }

    @Override
    public ResponseResult<OSRuntimeInfo> SystemRuntime() throws InterruptedException {
        OSRuntimeInfo osRuntimeInfo;
        return new ResponseResult<>(200, "获取成功", SystemInfoUtil.getOSRuntimeInfo());
    }
}
