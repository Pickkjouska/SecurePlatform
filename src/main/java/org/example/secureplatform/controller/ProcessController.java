package org.example.secureplatform.controller;

import org.checkerframework.checker.fenum.qual.PolyFenum;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/process")
public class ProcessController {
    @Autowired
    ProcessService processService;

    @GetMapping("/processInfo")
    public ResponseResult getProcesses() throws IOException {
        return processService.getProcesses();
    }
    @PostMapping("/delProcess")
    public ResponseResult delProcess(@RequestBody int pid) throws IOException {
        return processService.delProcesses(pid);
    }
    @GetMapping("/networkInfo")
    public ResponseResult getNetworkConnections() throws IOException {
        return processService.getNetworkConnections();
    }
}
