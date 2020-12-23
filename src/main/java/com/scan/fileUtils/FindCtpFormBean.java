package com.scan.fileUtils;

import com.scan.fileUtils.remove.MoveThread;
import com.scan.fileUtils.remove.ReMoveProject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 扫描表单中使用了ctp-form中的bean，用于添加到微服务spring-ctp-form.xml注入bean
 *
 */
public class FindCtpFormBean {

    private static List<String> dataList = new ArrayList<>();
    //结果文件路径
    private static String filePath = "C:\\Users\\ZHSHIRE\\Desktop\\工作文件\\老项目拆分微服务\\分库分表\\sp2圈表\\ctpFormBean.csv";

    static {
        dataList.add("className,user,fullName");
    }

    public static void main(String[] args) {

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            ExecutorService service = Executors.newFixedThreadPool(3);
            MoveThread moveThread = new MoveThread(ReMoveProject.microCapCore, ReMoveProject.oldFileMaps, countDownLatch);
            service.execute(moveThread);

            MoveThread moveThread2 = new MoveThread(ReMoveProject.microCapApi, ReMoveProject.oldFileMaps, countDownLatch);
            service.execute(moveThread2);
            //等待计数器归零
            countDownLatch.await();
            //cap-api使用ctp-form
            File f = new File("F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\cap-microservice-api\\src\\main\\java");
            Map<String, String> result = new HashMap<>(2048);
            testLoad(f, result);
            //cap-core使用ctp-form
            File f2 = new File("F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\cap-microservice-core\\src\\main\\java");
            Map<String, String> result2 = new HashMap<>(2048);
            testLoad(f2, result2);
            List<String> myList = dataList.stream().distinct().collect(Collectors.toList());
            CSVUtils.exportCsv(myList, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testLoad(File path, Map<String, String> result) {
        if (path.isFile()) {
            findBean(path);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                testLoad(f, result);
            }
        }
    }

    private static void findBean(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            List<String> content = IOUtils.readLines(is, "UTF-8");
            String user = null;
            for (int x = 0; x < content.size(); x++) {
                String s = content.get(x).trim();
                if (s.startsWith("package")) {
                    String pakage1 = s.replace("package", "").trim();
                    String pakage = pakage1.substring(0, pakage1.length() - 1);
                    String className = file.getName().replaceAll(".java", "");
                    user = pakage + "." + className;
                }
                if (s.startsWith("import") && s.contains("com.seeyon.ctp.form")) {
                    String str = s.replaceAll("import", "");
                    String fullName = str.substring(0, str.length() - 1).trim();
                    //ctp.form包在cap4中也有，排除掉cap4中的类
                    if (StringUtils.isBlank(ReMoveProject.oldFileMaps.get(fullName))) {
                        String[] className = s.split("[.]");
                        String className1 = className[className.length - 1].trim();
                        String beanName=className1.substring(0, className1.length() - 1);
                        //只统计manager和Dao层接口
                        if(beanName.endsWith("anager") || beanName.endsWith("DAO") ){
                            dataList.add(beanName+ "," + user + "," + fullName);
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
                } catch (Exception e) {

                }

            }
        }
    }


}
