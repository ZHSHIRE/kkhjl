package com.scan.fileUtils.remove;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-07 18:13
 **/
public class LoadFiles {
    //加载8.0sp1微服务项目文件
    public static void load(File path, Map fileMaps) {
        if (path.isFile()) {
            scanClass(path,fileMaps);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                load(f,fileMaps);
            }
        }
    }


    /**
     * 记录当前文件所属类和路径
     * @param file
     * @param fileMaps
     */
    public static void scanClass(File file, Map fileMaps) {
        InputStream is = null;
        try {
            if (file.getName().endsWith(".java")) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
                    if (s.startsWith("package")) {
                        String pakage1 = s.replace("package", "").trim();
                        String pakage = pakage1.substring(0, pakage1.length() - 1);
                        String className = file.getName().replaceAll(".java", "");
                        String fullName = pakage + "." + className;
                        //这里的路径得换成新的微服务项目地址根路径
                        fileMaps.put(fullName, file.getPath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //加载8.0sp2项目文件
    public static void move(File path, Map<String,String> fileMaps) {
        if (path.isFile()) {
            moveClass(path,fileMaps);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                load(f,fileMaps);
            }
        }
    }

    public static void moveClass(File file, Map<String,String> fileMaps) {
        InputStream is = null;
        try {
            if (file.getName().endsWith(".java")) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
                    if (s.startsWith("package")) {
                        String pakage1 = s.replace("package", "").trim();
                        String pakage = pakage1.substring(0, pakage1.length() - 1);
                        String className = file.getName().replaceAll(".java", "");
                        String fullName = pakage + "." + className;
                        if(StringUtils.isNotBlank(fileMaps.get(fullName))){
                            //移动
                            FileMove.copy(file,fileMaps.get(fullName));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
