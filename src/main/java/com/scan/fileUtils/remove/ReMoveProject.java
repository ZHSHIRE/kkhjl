package com.scan.fileUtils.remove;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.Move;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-07 16:07
 * 8.0sp1移动后的类在8.0SP2中同样移动
 * 1：三个项目的地址中的java文件都解析成map<全类名，位置>
 * 2：新的8.0SP2新建3个项目，获取每个java文件的全类名，然后查询map中的位置，然后根据位置移动
 **/
public class ReMoveProject {
    private static final String microCapCore = "F:\\ctp-microservice-bpm\\ctp-microservice-bpm\\cap-microservice-core\\src\\main\\java\\com\\seeyon";
    private static final String microCapApi = "F:\\ctp-microservice-bpm\\ctp-microservice-bpm\\cap-microservice-api\\src\\main\\java\\com\\seeyon";
    private static final String A8Controller = "F:\\ctp-microservice-v5-cap-core\\cap-core\\src\\main\\java";

    //已经拆过微服务8。0sp1项目的结构  第一个参数为类全名，第二个参数为当前类文件地址
    private static Map<String, String> oldFileMaps = new ConcurrentHashMap<>(1024);

    //8。0sp2项目的结构  第一个参数为类全名，第二个参数为当前类文件地址
    private static Map<String, String> newFileMaps = new HashMap<>(1024);

    public static void main(String[] args) {
        try {
            Long startTime = System.currentTimeMillis();
            final CountDownLatch countDownLatch = new CountDownLatch(3);
            ExecutorService service = Executors.newFixedThreadPool(3);
            MoveThread moveThread = new MoveThread(microCapCore, oldFileMaps, countDownLatch);
            service.execute(moveThread);

            MoveThread moveThread2 = new MoveThread(microCapApi, oldFileMaps, countDownLatch);
            service.execute(moveThread2);

            MoveThread moveThread3 = new MoveThread(A8Controller, oldFileMaps, countDownLatch);
            service.execute(moveThread3);
            //等待计数器归零
            countDownLatch.await();
            Long endTime = System.currentTimeMillis();
            Long haoshi = endTime - startTime;
            System.out.println("耗时1>>>>" + haoshi);
            System.out.println(oldFileMaps.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Long startTime2=System.currentTimeMillis();
        //load(new File(microCapCore));
        //load(new File(microCapApi));
        //load(new File(A8Controller));
        //Long endTime2=System.currentTimeMillis();
        //Long haoshi2=endTime2-startTime2;
        //System.out.println("耗时2>>>>"+haoshi2);
        //System.out.println(fileMaps.size());

        //根据路径移动位置
        LoadFiles.move(new File(""),oldFileMaps);
    }


}
