package com.scan.fileUtils.remove;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-09 19:31
 *
 * 微服务中删除controller类
 **/
public class DeleteController {
    private static final String codePath = "D:\\80SP2MicroService\\newMicroservice\\cap-microservice-core\\src\\main\\java";

    public static void main(String[] args) {
        try {
            //删除不是Controller的类
            deleteController(new File(codePath));
            //删除代码文件夹下的所有空文件夹
            //deleteFolder(codePath);
            System.out.println("删除完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历文件
     * @param file
     * @throws IOException
     */
    private static void deleteController(File file) throws IOException {
        if (file.isFile()) {
            scanFile(file);
        } else {
            File[] child = file.listFiles();
            for (File f : child) {
                deleteController(f);
            }
        }
    }

    /**
     * 删除文件
     * @param file
     * @throws IOException
     */
    private static void scanFile(File file) throws IOException {
        if (file.getName().endsWith("Controller.java")) {
            //    删除
            Files.delete(file.toPath());
        }
    }


}
