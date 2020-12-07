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
 * 扫描注入的外部bean  接口不在  com.seeyon.cap4和com.seeyon.ctp.form包下的
 * 这些外部bean需要做代理或者在当前spring mvc xml文件中注入bean
 */
public class FindBeanUtils {

    private static List<String> dataList = new ArrayList<>();
    //结果文件路径
    private static String filePath="C:\\Users\\ZHSHIRE\\Desktop\\Bean.csv";

    static {
        dataList.add("bean名,路径,使用类名");
    }

    public static void main(String[] args) {
        File f = new File("F:\\BreakProject\\microSeriviceA8\\cap-core\\src\\main\\java\\com\\seeyon\\cap4");
        Map<String, String> result = new HashMap<>(2048);
        testLoad(f, result);
        List<String> myList = dataList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(myList,filePath);
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
        String typeName = "";
        String beanName="";
        String refrencePath = "";
        InputStream is = null;
        try {
            Map<String, String> imports = new HashMap<>(1024);
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
                    typeName = as[1];
                    refrencePath = imports.get(typeName);
                    //如果map中不包含这个typeName则输出
                    if (StringUtils.isBlank(refrencePath)) {
                        System.out.println("当前类找不到import 手动确认 应该在本包下 路径为" + file.getAbsolutePath());
                        System.out.println("当前类找不到import 手动确认 应该在本包下 typeName：" + typeName + "/n");
                    } else if (!refrencePath.contains("com.seeyon.cap4") && !refrencePath.contains("com.seeyon.ctp.form")) {
                        //    写入csv
                        refrencePath = refrencePath.replaceAll("import", "");
                        //String data = beanName + "," + refrencePath + "," + file.getName();
                        String data = beanName + "," + refrencePath;
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
