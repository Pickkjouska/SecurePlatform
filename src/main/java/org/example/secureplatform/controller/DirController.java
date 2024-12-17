package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.example.secureplatform.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/dir")
public class DirController {

    @Autowired
    DirService dirService;
    @PostMapping("/search")
    public ResponseResult<DirInfo> searchdir(@RequestBody DirRequest dirRequest) {
        return dirService.SearchDir(dirRequest);
    }
}
