package org.example.secureplatform.service.Impl;

import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.util.DirInfoUtil;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.example.secureplatform.service.DirService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DirServiceImpl extends DirService {

    @Override
    public ResponseResult<DirInfo> SearchDir(DirRequest dirRequest) {
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        File folder = dirpath.toFile();
        // 获取文件路径信息
        DirInfo dirinfo = DirInfoUtil.getDirInfo(dirpath);
        if (folder.isDirectory()) {
            // 获取目录中的所有文件/文件夹
            String[] files = folder.list();
            if (files != null) {
                List<String> sortedFiles = new ArrayList<>(Arrays.asList(files));
                // 按照文件类型排序，文件夹在前，文件在后
                sortedFiles.sort((file1, file2) -> {
                    Path path1 = Paths.get(dirpath.toString(), file1).normalize();
                    Path path2 = Paths.get(dirpath.toString(), file2).normalize();
                    boolean isDir1 = Files.isDirectory(path1);
                    boolean isDir2 = Files.isDirectory(path2);
                    if (isDir1 && !isDir2) {
                        return -1; // 文件夹在前
                    } else if (!isDir1 && isDir2) {
                        return 1; // 文件在后
                    } else {
                        return 0; // 相等
                    }
                });
                // 计算分页参数
                int pageSize = dirRequest.getPageSize(); // 前端传递的每页显示数量
                int page = dirRequest.getPage(); // 前端传递的当前页码
                int fromIndex = (page - 1) * pageSize; // 当前页的起始索引
                int toIndex = Math.min(fromIndex + pageSize, files.length); // 当前页的结束索引
                List<DirInfo> fileInfo = new ArrayList<>();
                for (int i = fromIndex; i < toIndex; i++) {
                    // 将基础路径与当前文件/文件夹的相对路径结合起来
                    String fileName = sortedFiles.get(i);
                    Path filePath = Paths.get(dirpath.toString(), fileName).normalize();
                    // 获取子目录路径信息
                    fileInfo.add(DirInfoUtil.getDirInfo(filePath));
                }
                dirinfo.setItem(fileInfo);
                dirinfo.setItemTotal(files.length);
            }
        }
        return new ResponseResult<>(200, "获取成功", dirinfo);
    }

    // 获取文件信息
    @Override
    public ResponseResult<DirInfo> Content(DirRequest dirRequest) {
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        DirInfo dirinfo = DirInfoUtil.getFileInfo(dirpath);
        return new ResponseResult<>(200, "获取成功", dirinfo);
    }

    // 增加新文件
    @Override
    public ResponseResult<String> files(DirRequest dirRequest) throws IOException {
        String data = DirInfoUtil.Createfile(dirRequest);
        return new ResponseResult<>(200, "获取成功", data);
    }

    // 删除
    @Override
    public ResponseResult<Boolean> del(DirRequest dirRequest) throws IOException {
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        File file = dirpath.toFile();
        if (Objects.equals(dirRequest.getIsDir(), "false")) {
            DirInfoUtil.DeleteFile(file);
        } else {
            DirInfoUtil.DeleteDir(file);
        }
        return new ResponseResult<>(200, "获取成功");
    }

    // 保存
    @Override
    public ResponseResult<DirInfo> save(DirRequest dirRequest) throws IOException {
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        DirInfoUtil.SaveFile(dirpath, dirRequest.getContent());
        return new ResponseResult<>(200, "获取成功");
    }

    @Override
    public ResponseResult<String> rename(DirRequest dirRequest) throws IOException {
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        String data =  DirInfoUtil.RenameFile(dirpath, dirRequest.getOldName(), dirRequest.getNewName());

        return new ResponseResult<>(200, "获取成功", data);
    }

    @Override
    public ResponseResult<String> upload(DirRequest dirRequest, MultipartFile file) throws IOException {
        String data = DirInfoUtil.uploadFile(dirRequest.getPath(), file);
        return new ResponseResult<>(200, "获取成功", data);
    }

    @Override
    public ResponseResult<String> download(String filename, HttpServletResponse response) {
        String data = DirInfoUtil.downLoad(filename, response);
        return new ResponseResult<>(200, "获取成功", data);
    }
}
