package com.scan.fileUtils;

import com.seeyon.ctp.common.AppContext;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-07 15:21
 *在微服务中调用todo方法，结果为微服务需要代理A8中的bean，或者A8代理微服务的bean，和A8和微服务都不存在的bean需要注意
 **/
public class A8NeadProxy {

    //使用AppContext.getBean()的集合
    private static List<String> appcontextGetBeanList = new ArrayList<>();

    //使用AppContext.getBean()的集合
    private static List<String> otherAppcontextGetBeanList = new ArrayList<>();

    //使用AppContext.getBeansTypeof()的集合
    private static List<String> appcontextGetBeansTypeofList = new ArrayList<>();

    //使用AppContext.getBean()的集合
    private static List<String> otherAppcontextGetBeansTypeofList = new ArrayList<>();
    //V5BeanUnll.txt和v5BeansTypeUnll.txt所在文件
    private static final String projectPath = "C:\\Users\\ZHSHIRE\\Desktop\\V5";
    //在A8中这个bean也为空
    private static final String GetBeanResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\AllNamesUnll.csv";

    //AppContext.getBean()的结果文件路径
    private static final String otherGetBeanResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\V5needPoxyMsNames.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String otherGetBeansTypeofResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\V5needPoxyMsTypes.csv";

    //在A8中这个beansOfType也为空
    private static final String getBeansTypeofResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\AllTypesNull.csv";

    private static void testLoad(File path) {
        if (path.isFile()) {
            findBean(path);
            appcontextGetBeanList.add("code,isNull");
            appcontextGetBeansTypeofList.add("code,isNull");
            otherAppcontextGetBeanList.add("code,isNull,class");
            otherAppcontextGetBeansTypeofList.add("code,isNull,calss");
            File f = new File(projectPath);
            testLoad(f);
            //去重
            List<String> getBeanList = appcontextGetBeanList.stream().distinct().collect(Collectors.toList());
            CSVUtils.exportCsv(getBeanList, GetBeanResFilePath);

            List<String> getBeansOfTypeList = appcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
            CSVUtils.exportCsv(getBeansOfTypeList, getBeansTypeofResFilePath);

            //去重
            List<String> getBeanList3 = otherAppcontextGetBeanList.stream().distinct().collect(Collectors.toList());
            CSVUtils.exportCsv(getBeanList3, otherGetBeanResFilePath);

            //去重
            List<String> getBeanList4 = otherAppcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
            CSVUtils.exportCsv(getBeanList4, otherGetBeansTypeofResFilePath);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                testLoad(f);
            }
        }
    }

    private static void findBean(File file) {
        InputStream is = null;
        try {
            //V5BeanUnll.txt 为微服务或A8中AppContext.getBean()为空的bean文件，每一行一个beanId
            if ("V5BeanUnll.txt".equals(file.getName())) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
                    s.replace(";", "");
                    if (AppContext.getBean(s) == null) {
                        appcontextGetBeanList.add(s + ",true");
                    } else {   //当前微服务有的，要代理
                        String packege = "";
                        try {
                            packege = AppContext.getBean(s).getClass().getName();
                        } catch (Exception e) {
                        }
                        otherAppcontextGetBeanList.add(s + ",false" + "," + packege);
                    }
                }
            }
            //v5BeansTypeUnll.txt 为微服务或A8中AppContext.getBeansOfType()为空的type文件，每一行一个type接口的全类名
            if ("v5BeansTypeUnll.txt".equals(file.getName())) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    try {
                        String s = content.get(x).trim();
                        String type = s.replace(".class", "");
                        if (AppContext.getBeansOfType(Class.forName(type)) == null || AppContext.getBeansOfType(Class.forName(type)).size() < 1) {
                            appcontextGetBeansTypeofList.add(type + ",true");
                        } else {   //当前有的，要代理
                            String packege = "";
                            try {
                                packege = AppContext.getBean(s).getClass().getName();
                            } catch (Exception e) {

                            }
                            otherAppcontextGetBeansTypeofList.add(type + ",false" + "," + packege);
                        }
                    } catch (Exception e) {
                        System.out.println("type异常");
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
                    System.out.println("");
                }

            }
        }
    }


}
