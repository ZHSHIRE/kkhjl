package com.scan.fileUtils.remove;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-03 10:42
 **/

public class FileMove {

    /**
     * 创建父级文件夹
     *
     * @param file
     */
    public static void createParentPath(File file) {
        File parentFile = file.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            parentFile.mkdirs(); // 创建文件夹
            createParentPath(parentFile); // 递归创建父级目录
        }
    }

    /**
     * 文件夹移动
     * @param file
     * @param dest
     * @throws IOException
     */
    public static void copy(File file, String dest) throws IOException {
        File destFile = new File(dest);
        createParentPath(destFile);
        //不是目录的情况下进行文件复制操作
        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        //Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

    }

}
