package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.osinfo.OSInfo;
import org.example.secureplatform.entity.osinfo.OSRuntimeInfo;
import org.example.secureplatform.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemInfoController {

    @Autowired
    SystemInfoService systemInfoService;

    @GetMapping("/osinfo")
    public ResponseResult<OSInfo> osinfo() {
        return systemInfoService.SystemInfo();
    }

    @GetMapping("/osruntime")
    public ResponseResult<OSRuntimeInfo> osruntime() throws InterruptedException { return systemInfoService.SystemRuntime(); }
}
