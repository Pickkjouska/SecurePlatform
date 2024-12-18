package org.example.secureplatform.common;

import org.apache.tomcat.jni.FileInfo;
import org.example.secureplatform.entity.files.DirInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DirInfoUtil {
    public static String toOctalMode(Set<PosixFilePermission> permissions) {
        int padding = 0;
        int owner = 0;
        int group = 0;
        int others = 0;

        // 遍历权限集，将每个权限对应的标志转换为位
        for (PosixFilePermission permission : permissions) {
            if (permission.equals(PosixFilePermission.OWNER_READ)) {
                owner |= 4;  // rwx -> 4
            } else if (permission.equals(PosixFilePermission.OWNER_WRITE)) {
                owner |= 2;  // rwx -> 2
            } else if (permission.equals(PosixFilePermission.OWNER_EXECUTE)) {
                owner |= 1;  // rwx -> 1
            } else if (permission.equals(PosixFilePermission.GROUP_READ)) {
                group |= 4;
            } else if (permission.equals(PosixFilePermission.GROUP_WRITE)) {
                group |= 2;
            } else if (permission.equals(PosixFilePermission.GROUP_EXECUTE)) {
                group |= 1;
            } else if (permission.equals(PosixFilePermission.OTHERS_READ)) {
                others |= 4;
            } else if (permission.equals(PosixFilePermission.OTHERS_WRITE)) {
                others |= 2;
            } else if (permission.equals(PosixFilePermission.OTHERS_EXECUTE)) {
                others |= 1;
            }
        }
        // 拼接八进制表示
        return String.format("%03o", padding) + String.format("%03o", owner) + String.format("%03o", group) + String.format("%03o", others);
    }


    public static DirInfo getDirInfo(Path dirpath) {
        File folder = dirpath.toFile();
        DirInfo dirinfo = new DirInfo();

        try {
            // 获取文件的绝对路径
            dirinfo.setPath(folder.getAbsolutePath());
            // 获取文件名称
            dirinfo.setName(folder.getName());
            // 获取文件权限
            FileOwnerAttributeView ownerAttr = Files.getFileAttributeView(dirpath, FileOwnerAttributeView.class);
            UserPrincipal userPrincipal = ownerAttr.getOwner();
            dirinfo.setUser(userPrincipal.getName());
            // Linux下获取文件组信息
//            GroupPrincipal group = Files.readAttributes(dirpath, PosixFileAttributes.class).group();
//            dirinfo.setGroup(group.getName());
            dirinfo.setGroup("hhh");
            // 获取文件大小
            dirinfo.setSize(String.valueOf(Files.size(dirpath)));
            // 判断是否为符号链接
            dirinfo.setIsSymlink(Files.isSymbolicLink(dirpath) ? "true" : "false");
            // 判断是否为隐藏文件
            dirinfo.setIsHidden(Files.isHidden(dirpath) ? "true" : "false");
            /**
             * 链接路径
            **/
//            if (Objects.equals(dirinfo.getIsSymlink(), "true")) {
//                dirinfo.setLinkPath(Files.readSymbolicLink(dirpath));
//            }
            //权限等级
//            PosixFileAttributes attrs = Files.readAttributes(dirpath, PosixFileAttributes.class);
//            dirinfo.setMode(toOctalMode(attrs.permissions()));

            dirinfo.setMode("0700");
            // 获取文件的最后修改时间
            dirinfo.setUpdateTime(Files.getLastModifiedTime(dirpath).toString());
            // 判断是否为文件夹
            dirinfo.setIsDir(folder.isDirectory() ? "true" : "false");

        } catch (Exception e){
            e.printStackTrace();
        }
        return dirinfo;
    }

    // 获取文件信息
    public static DirInfo getFileInfo(Path dirpath) {
        DirInfo dirinfo = getDirInfo(dirpath);
        try{
            StringBuilder content = new StringBuilder();
            String mimeType = Files.probeContentType(dirpath);
            List<String> lines = Files.readAllLines(dirpath);
            for (String line : lines) {
                content.append(line).append("\n");
            }
            dirinfo.setMimeType(mimeType);
            dirinfo.setContent(content.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return dirinfo;
    }

    // 创建文件或目录
    public static String Createfile(Path dirpath, String isDir) throws IOException {
        String response = "";
        if (isDir.equals("true")) {
            if (!Files.exists(dirpath)) {
                // 创建空目录
                Files.createDirectories(dirpath);
                response = "目录已创建";
            } else {
                response = "目录已经存在";
            }
        } else {
            if (!Files.exists(dirpath)) {
                // 创建空文件
                Files.createFile(dirpath);
                response = "文件已创建";
            } else {
                response = "文件已经存在";
            }
        }
        return response;
    }

    // 删除文件
    public static void DeleteFile(File folder) throws IOException {
        folder.delete();
    }

    // 删除目录
    public static void DeleteDir(File folder) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                DeleteDir(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }

    // 保存文件
    public static void SaveFile(Path dirpath, String content) throws IOException {
        Files.write(dirpath, content.getBytes(), StandardOpenOption.CREATE);
    }

    // 重命名
    public static String RenameFile(Path dirpath, String oldName, String newName) throws IOException {
        String response = "";
        if (Objects.equals(oldName, newName)) {
            response = "文件名重复";
        } else {
            Files.move(dirpath, Paths.get(newName));
            response = "修改成功";
        }
        return response;
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(getDirInfo(Path.of("D:/springproject/SecurePlatform")));
//        System.out.println(getFileInfo(Path.of("D:/springproject/SecurePlatform/text.txt")));
//        Createfile(Path.of("D:/springproject/SecurePlatform/ddir"), "false");
        File file = Path.of("D:/springproject/SecurePlatform/ddir").toFile();
        DeleteDir(file);
    }
}
