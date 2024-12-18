package org.example.secureplatform.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public abstract class DirService {

    public abstract ResponseResult<DirInfo> SearchDir(DirRequest dirrequest);

    public abstract ResponseResult<DirInfo> Content(DirRequest dirRequest);

    public abstract ResponseResult<String> files(DirRequest dirRequest) throws IOException;

    public abstract ResponseResult<Boolean> del(DirRequest dirRequest) throws IOException;

    public abstract ResponseResult<DirInfo> save(DirRequest dirRequest) throws IOException;

    public abstract ResponseResult<String> rename(DirRequest dirRequest) throws IOException;

    public abstract ResponseResult<String> upload(DirRequest dirRequest, MultipartFile file) throws IOException;

    public abstract ResponseResult<String> download(String filename, HttpServletResponse response);
}
