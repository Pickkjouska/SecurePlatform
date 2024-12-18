package org.example.secureplatform.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.example.secureplatform.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/public/dir")
public class DirController {

    @Autowired
    DirService dirService;
    @PostMapping("/search")
    public ResponseResult<DirInfo> searchdir(@RequestBody DirRequest dirRequest) {
        return dirService.SearchDir(dirRequest);
    }

    @PostMapping("/content")
    public ResponseResult<DirInfo> content(@RequestBody DirRequest dirRequest) {
        return dirService.Content(dirRequest);
    }

    @PostMapping("/files")
    public ResponseResult<String> files(@RequestBody DirRequest dirRequest) throws IOException {
        return dirService.files(dirRequest);
    }

    @PostMapping("/del")
    public ResponseResult<Boolean> del(@RequestBody DirRequest dirRequest) throws IOException {
        return dirService.del(dirRequest);
    }

    @PostMapping("/save")
    public ResponseResult<DirInfo> save(@RequestBody DirRequest dirRequest) throws IOException {
        return dirService.save(dirRequest);
    }

    @PostMapping("/rename")
    public ResponseResult<String> rename(@RequestBody DirRequest dirRequest) throws IOException {
        return dirService.rename(dirRequest);
    }

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestBody DirRequest dirRequest, MultipartFile file) throws IOException {
        return dirService.upload(dirRequest, file);
    }

    @GetMapping("/download")
    public ResponseResult<String> download(@RequestParam String filename, HttpServletResponse response) {
        return dirService.download(filename, response);
    }
}
