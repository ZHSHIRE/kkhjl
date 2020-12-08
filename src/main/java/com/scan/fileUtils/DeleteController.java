package com.scan.fileUtils;

import org.junit.Test;
import java.io.File;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-08 11:21
 * 拆项目后，将微服务中controller删掉，
 **/
public class DeleteController {
    @Test
    public void getFilE() {
        getAllFileName("F:\\BreakProject\\cap-microservice-core\\cap-microservice-core\\src\\main\\java\\com\\seeyon");
    }

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path) {

        boolean flag = false;
        File file = new File(path);
        //clear(file);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if(tempList[i].getAbsolutePath().contains("Controller.java")){
                deleteFile(tempList[i].getAbsolutePath());
                }
            }
            if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
                getAllFileName(tempList[i].getAbsolutePath());
            }
        }
    }


    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static void deleteFile(String sPath) {

        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }
}
