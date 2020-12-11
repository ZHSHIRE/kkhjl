package com.scan.fileUtils.remove;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-07 17:01
 **/
public class MoveThread implements Runnable {
    private String path;
    private Map fileMaps;
    private CountDownLatch countDownLatch;
    public MoveThread(String path, Map fileMap,CountDownLatch countDownLatch){
        this.path=path;
        this.fileMaps=fileMap;
        this.countDownLatch=countDownLatch;
    }
    @Override
    public void run() {
        try {
            LoadFiles.load(new File (path),fileMaps);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //减一
            countDownLatch.countDown();
        }

    }
}
