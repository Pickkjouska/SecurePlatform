package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.DirInfoUtil;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.example.secureplatform.service.DirService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DirServiceImpl extends DirService {

    @Override
    public ResponseResult<DirInfo> SearchDir(DirRequest dirrequest) {
        Path dirpath = Paths.get(dirrequest.getPath()).normalize();
        File folder = dirpath.toFile();
        // 获取文件路径信息
        DirInfo dirinfo = DirInfoUtil.getFileInfo(dirpath);
//        System.out.println(DirInfoUtil.getFileInfo(dirpath));
        if (folder.isDirectory()) {
            // 获取目录中的所有文件/文件夹
            String[] files = folder.list();
            if (files != null) {
                List<DirInfo> fileInfo = new ArrayList<>();
                for (String file : files) {
                    // 将基础路径与当前文件/文件夹的相对路径结合起来
                    Path filePath = Paths.get(dirpath.toString(), file).normalize();
                    // 获取子目录路径信息
                    fileInfo.add(DirInfoUtil.getFileInfo(filePath));
                }
                dirinfo.setItem(fileInfo);
            }
        }
        return new ResponseResult<>(200, "获取成功", dirinfo);
    }
}
