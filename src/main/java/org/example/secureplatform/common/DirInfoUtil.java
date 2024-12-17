package org.example.secureplatform.common;

import org.apache.tomcat.jni.FileInfo;
import org.example.secureplatform.entity.files.DirInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    public static DirInfo getFileInfo(Path dirpath) {
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
    public static void main(String[] args) throws Exception {
        System.out.println(getFileInfo(Path.of("D:/springproject/SecurePlatform")));
    }
}
