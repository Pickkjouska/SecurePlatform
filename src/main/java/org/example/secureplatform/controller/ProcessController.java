package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
