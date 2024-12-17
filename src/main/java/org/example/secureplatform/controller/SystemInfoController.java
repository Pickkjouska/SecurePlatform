package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.common.SystemInfoUtil;
import org.example.secureplatform.entity.osinfo.OSInfo;
import org.example.secureplatform.entity.osinfo.OSRuntimeInfo;
import org.example.secureplatform.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.hardware.NetworkIF;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SystemInfoController {

    @Autowired
    SystemInfoService systemInfoService;

    @GetMapping("/osinfo")
    public ResponseResult<OSInfo> osinfo() {
        return systemInfoService.SystemInfo();
    }

    @GetMapping("/test")
    public ResponseResult<List<NetworkIF>> sad(){
        return new ResponseResult<>(200, "sd", SystemInfoUtil.getNetwork());
    }

    @GetMapping("/osruntime")
    public ResponseResult<OSRuntimeInfo> osruntime() throws InterruptedException { return systemInfoService.SystemRuntime(); }
}
