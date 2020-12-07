package com.scan.fileUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: ZHSHIRE
 * @create: 2020-11-16 17:44
 * 扫描出controller中使用@Inject注解注入的bean
 **/
public class ControllerUserBean {

    private static List<String> dataList = new ArrayList<>();

    static {
        dataList.add("beanId,pakage,User");
    }
    private static Map<String, String> imports = new HashMap<>(1024);
    public static void main(String[] args) {
        File f = new File("F:\\BreakProject\\microSeriviceA8\\cap-core\\src\\main\\java\\com\\seeyon\\cap4");
        //Map<String, String> imports = new HashMap<>(1024);
        testLoad(f);
        List<String> myList = dataList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(myList,"");
    }

    private static void testLoad(File path) {
        if (path.isFile()) {
            findBean(path);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                testLoad(f);
            }
        }
    }

    private static void findBean(File file) {
        String typeName = "";
        String beanName="";
        String refrencePath = "";
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            List<String> content = IOUtils.readLines(is, "UTF-8");
            for (int x = 0; x < content.size(); x++) {
                String s = content.get(x).trim();
                if (s.startsWith("import")) {
                    String[] className = s.split("[.]");
                    String className1 = className[className.length - 1].trim();
                    imports.put(className1.substring(0, className1.length() - 1), s.trim());
                    continue;
                }
                if (s.contains("@Inject")) {
                    String privateContent = content.get(x + 1).trim();
                    if(privateContent.contains("@")){
                        privateContent = content.get(x + 2).trim();
                    }
                    //System.out.println(privateContent);
                    String[] as = privateContent.split("\\s+");
                    beanName=as[as.length-1];
                    //去掉最后的分号
                    beanName=beanName.substring(0,beanName.length()-1);
                    //.substring(0,beanName.length()-1)
                    typeName = as[1].replaceAll(";","");
                    refrencePath = imports.get(typeName);
                    //如果map中不包含这个typeName则输出
                    if (StringUtils.isBlank(refrencePath)) {
                        System.out.println("当前类找不到import 手动确认 应该在本包下 路径为" + file.getAbsolutePath());
                        System.out.println("当前类找不到import  手动确认 应该在本包下 typeName：" + typeName + "/n");
                    } else{
                        //    写入csv
                        refrencePath = refrencePath.replaceAll("import", "").replaceAll(";", "").trim();
                        String data = beanName + "," + refrencePath + "," + file.getName();
                        //String data = beanName + "," + refrencePath;
                        dataList.add(data);
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
