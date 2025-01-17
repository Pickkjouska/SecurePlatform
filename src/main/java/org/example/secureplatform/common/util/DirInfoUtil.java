package org.example.secureplatform.common.util;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DirInfoUtil {
    private static final Logger log = LogManager.getLogger(DirInfoUtil.class);

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
            GroupPrincipal group = Files.readAttributes(dirpath, PosixFileAttributes.class).group();
            dirinfo.setGroup(group.getName());
            // 获取文件大小
            dirinfo.setSize(String.valueOf(Files.size(dirpath)));
            // 判断是否为符号链接
            dirinfo.setIsSymlink(Files.isSymbolicLink(dirpath) ? "true" : "false");
            // 判断是否为隐藏文件
            dirinfo.setIsHidden(Files.isHidden(dirpath) ? "true" : "false");
            /**
             * 链接路径
            **/
            if (Objects.equals(dirinfo.getIsSymlink(), "true")) {
                dirinfo.setLinkPath(Files.readSymbolicLink(dirpath));
            }
//            权限等级
            PosixFileAttributes attrs = Files.readAttributes(dirpath, PosixFileAttributes.class);
            dirinfo.setMode(toOctalMode(attrs.permissions()));
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
    public static String Createfile(DirRequest dirRequest) throws IOException {
        String response = "";
        String isDir = dirRequest.getIsDir();
        Path dirpath = Paths.get(dirRequest.getPath()).normalize();
        String isLink = dirRequest.getIsLink();
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(convertOctalToPosixPermission(dirRequest.getPermission()));
        FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
        if (isDir.equals("true")) {
            if (!Files.exists(dirpath)) {
                // 创建空目录
                Files.createDirectories(dirpath, fileAttributes);
                response = "目录已创建";
            } else {
                response = "目录已经存在";
            }
        } else {
            if (!Files.exists(dirpath)) {
                // 创建空文件
                if (Objects.equals(isLink, "true")) {
                    try {
                        Path target = Paths.get(dirRequest.getLinkPath());
                        System.out.println(" dirpath" + dirpath + " target " + target + "type" + dirRequest.getLinkType());
                        createLink(dirpath, target, dirRequest.getLinkType());
                    } catch (Exception e) {
                        log.error("e: ", e);
                    }
                    response = "链接文件已创建";
                } else {
                    Files.createFile(dirpath, fileAttributes);
                    response = "文件已创建";
                }
            } else {
                response = "文件已经存在";
            }
        }
        return response;
    }

    public  static void createLink(Path dirpath, Path Link, String linkType) throws IOException {
        if (Objects.equals(linkType, "SymbolicLink")) {
            Files.createSymbolicLink(dirpath, Link);
            System.out.println("Symbolic link created from " + dirpath + " to " + Link);
        } else if (Objects.equals(linkType, "Link")) {
            Files.createLink(dirpath, Link);
            System.out.println("Link created from " + dirpath + " to " + Link);
        } else {
            System.out.println("错误");
            System.out.println(linkType.equals("SymbolicLink"));
        }
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
        try {
            Files.write(dirpath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("文件保存成功: " + dirpath);
        } catch (IOException e) {
            // 处理写入文件时的异常
            System.err.println("保存文件时发生错误: " + e.getMessage());
            throw e; // 可以选择重新抛出异常，或者处理它
        }
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

    // 文件上传
    public static String uploadFile(String dirpath, MultipartFile file) throws IOException {
        String response = "上传成功";
        try {
            // 文件的原始名称
            String originalFilename = file.getOriginalFilename();
            // 文件的主名称
            String mainName = FileUtil.mainName(originalFilename);
            // 文件的扩展名(后缀)
            String extName = FileUtil.extName(originalFilename);
            if (FileUtil.exist(dirpath + File.separator + originalFilename)){
                originalFilename = System.currentTimeMillis() + "-" + mainName + "." + extName;
            }
            File saveFile = new File(dirpath + File.separator + originalFilename);
            file.transferTo(saveFile);
        } catch (Exception e) {
            return "文件上传失败：" + e.getMessage();
        }
        return response;
    }

    public static HttpServletResponse downLoad(String filename, HttpServletResponse response){
        File file = new File(filename);
        String extName = FileUtil.extName(file);
        if (!file.exists() || !file.isFile()) {
            // 如果文件不存在，返回一个适当的错误响应
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            // 设置响应头：告知浏览器这是一个文件下载请求
            switch (extName) {
                case "pdf":
                    response.setContentType("application/pdf");
                    break;
                case "jpg":
                case "jpeg":
                    response.setContentType("image/jpeg");
                    break;
                case "png":
                    response.setContentType("image/png");
                    break;
                case "txt":
                    response.setContentType("text/plain");
                    break;
                default:
                    response.setContentType("application/octet-stream");
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            // 设置响应文件的长度
            response.setContentLength((int) file.length());
            // 读取文件并写入到响应流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            // 刷新流，确保所有数据都已写入响应
            outputStream.flush();
        } catch (IOException e) {
            // 如果发生异常，可以返回错误信息
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return response;
    }
    public static String convertOctalToPosixPermission(String octalPermission) {
        // 验证传入的字符串是否为有效的八进制权限（3-4 位的数字，且只能包含0-7）
        if (!octalPermission.matches("[0-7]{3,4}")) {
            throw new IllegalArgumentException("Permission must be a 3 or 4-digit octal string (e.g., 0755 or 644).");
        }

        // 转换为整数（八进制）
        int permission = Integer.parseInt(octalPermission, 8);

        // 计算 POSIX 权限字符串
        StringBuilder sb = new StringBuilder();
        String[] perms = {"---", "--x", "-w-", "-wx", "r--", "r-x", "rw-", "rwx"};

        sb.append(perms[(permission >> 6) & 7]) // Owner 权限
                .append(perms[(permission >> 3) & 7]) // Group 权限
                .append(perms[permission & 7]);       // Others 权限

        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
//        System.out.println(getDirInfo(Path.of("D:/springproject/SecurePlatform")));
//
//        Createfile(Path.of("D:/springproject/SecurePlatform/ddir"), "false");
//        File file = Path.of("D:/springproject/SecurePlatform/ddir").toFile();
//        DeleteDir(file);
//        Path dirpath = Paths.get("D:/springproject/SecurePlatform/text/asd.txt");
//        Path das = Paths.get("D:/springproject/SecurePlatform/text/xxxxx.txt");
//        createLink(dirpath, das, "SymbolicLink");
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(convertOctalToPosixPermission("0755"));
        FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
        System.out.println(fileAttributes);
    }
}
