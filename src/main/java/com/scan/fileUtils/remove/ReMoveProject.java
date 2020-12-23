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
 * 3：程序移动前需要将8.0sp2develop cap-api复制一份并冲命名为microservice-cap-api，将8.0sp2develop cap-core移动的文件移动到microservice-cap-api
 * 4：将移动剩下的项目作为微服务，并将这个微服务代码中删掉所有不是controller的类，形成A8 controller
 * 5：将移动剩下的项目作为微服务，删掉controller类，形成微服务
 **/
public class ReMoveProject {
    public static final String microCapCore = "F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\cap-microservice-core\\src\\main\\java";
    public static final String microCapApi = "F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\cap-microservice-api\\src\\main\\java";
    //public static final String A8Controller = "F:\\ctp-microservice-v5-cap-core\\cap-core\\src";

    //已经拆过微服务8。0sp1项目的结构  第一个参数为类全名，第二个参数为当前类文件地址
    public static Map<String, String> oldFileMaps = new ConcurrentHashMap<>();

    public static List<String> duble=new ArrayList<>();
    //
    ////8。0sp2项目的结构  第一个参数为类全名，第二个参数为当前类文件地址
    //private static Map<String, String> newFileMaps = new HashMap<>(1024);

    public static void main(String[] args) {
        try {
            Long startTime = System.currentTimeMillis();
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            ExecutorService service = Executors.newFixedThreadPool(3);
            MoveThread moveThread = new MoveThread(microCapCore, oldFileMaps, countDownLatch);
            service.execute(moveThread);

            MoveThread moveThread2 = new MoveThread(microCapApi, oldFileMaps, countDownLatch);
            service.execute(moveThread2);

            //MoveThread moveThread3 = new MoveThread(A8Controller, oldFileMaps, countDownLatch);
            //service.execute(moveThread3);
            //等待计数器归零
            countDownLatch.await();

            Long endTime = System.currentTimeMillis();
            Long haoshi = endTime - startTime;
            System.out.println("耗时1>>>>" + haoshi);
            System.out.println(oldFileMaps.size());
            service.shutdown();
            //根据路径移动位置
            //LoadFiles.move(new File("D:\\80SP2MicroService\\80sp2\\cap-api"),oldFileMaps);
            LoadFiles.move(new File("D:\\80SP2MicroService\\80sp2\\cap-core"),oldFileMaps);
            System.out.println("移动完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
