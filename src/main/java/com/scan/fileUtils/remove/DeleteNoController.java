package com.scan.fileUtils.remove;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.apache.poi.hemf.record.emfplus.HemfPlusRecordType.clear;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-09 17:24
 * 将cap-core中的类不是Controller的类删掉，成为A8的controller
 **/
public class DeleteNoController {
    private static final String codePath = "D:\\80SP2MicroService\\newMicroservice\\A8Controller\\A8-cap-core\\src\\main\\java";

    public static void main(String[] args) {
        try {
            //删除不是Controller的类
            //deleteClass(new File(codePath));
            //删除代码文件夹下的所有空文件夹
            deleteFolder(codePath);
            System.out.println("删除完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //文件
    private static void deleteClass(File file) throws IOException {
        if (file.isFile()) {
            scanFile(file);
        } else {
            File[] child = file.listFiles();
            for (File f : child) {
                deleteClass(f);
            }
        }
    }

    private static void scanFile(File file) throws IOException {

        if (!file.getName().endsWith("Controller.java")) {
            //    删除
            Files.delete(file.toPath());
            //System.out.println("删除类》》》》》" + file.getName());
        }
    }


    /**
     * 删除路径下所有空文件夹，用于A8 controller删除无关类
     *
     * @param path 将要删除空文件夹的路径
     */
    public static void deleteFolder(String path) {
        File folder = new File(path);
        File [] files=folder.listFiles();
        if(files !=null) {
            for (File f:files) {
                if (f.isDirectory()) {
                    deleteFolder(f.getPath());
                }
            }
        }
        //如果为空文件夹
        if(files.length<1) {
            folder.delete();
            //子文件删除后。父文件可能为空，将父文件也删除
            if(folder.getParentFile().listFiles().length<1){
                folder.getParentFile().delete();
            }
        }
    }
}
