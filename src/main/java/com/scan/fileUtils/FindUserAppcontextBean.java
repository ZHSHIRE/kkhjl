package com.scan.fileUtils;

import org.apache.commons.io.IOUtils;

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
 * @create: 2020-11-11 15:42
 * 查找当前项目的AppContext.getBean()和AppContext.getBeansOfType()
 **/
public class FindUserAppcontextBean {
    //使用AppContext.getBean()的集合
    private static List<String> appcontextGetBeanList = new ArrayList<>();
    //使用AppContext.getBeansTypeof()的集合
    private static List<String> appcontextGetBeansTypeofList = new ArrayList<>();
    //q读取项目路径
    private static final String projectPath = "C:\\Users\\ZHSHIRE\\Desktop\\mxForm";
    //AppContext.getBean()的结果文件路径
    private static final String GetBeanResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\FormAppContextGetBean.csv";
    //AppContext.getBeansTypeof()的结果文件路径
    private static final String getBeansTypeofResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\FormAppContextBeansTypeOfBean.csv";

    static {
        appcontextGetBeanList.add("code,bean");
        appcontextGetBeansTypeofList.add("code,package");
    }

    public static void main(String[] args) {
        File f = new File(projectPath);
        testLoad(f);
        //去重
        List<String> getBeanList = appcontextGetBeanList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeanList, GetBeanResFilePath);

        List<String> getBeansOfTypeList = appcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeansOfTypeList, getBeansTypeofResFilePath);
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
        InputStream is = null;
        try {
            if (file.getName().endsWith(".java")) {
                String getBean = "AppContext.getBean(";
                String getType = "AppContext.getBeansOfType(";
                //局部变量，自动回收
                Map<String, String> imports = new HashMap<>(1024);
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
                    s.replace(";", "");
                    //将数据类型加入
                    if (s.startsWith("package")) {
                        String pakage1 = s.replace("package", "").trim();
                        String pakage = pakage1.substring(0, pakage1.length() - 1);
                        imports.put("pk", pakage.trim());
                        continue;
                    }
                    if (s.startsWith("import")) {
                        s = s.replaceAll("import", "");
                        String[] className = s.split("[.]");
                        String className1 = className[className.length - 1].trim();
                        imports.put(className1.substring(0, className1.length() - 1), s.substring(0, s.length() - 1).trim());
                        continue;
                    }
                    if (s.contains(getBean) && !s.startsWith("//")) {
                        //获取AppContext.getBean(首次出现的位置
                        int start = s.indexOf(getBean);
                        //获取反括号的位置
                        int end = s.indexOf(")", start);
                        String getBeanContext = s.substring(start, end + 1);
                        String context="";
                        try {
                            if(getBeanContext.equals("AppContext.getBean(s)")){
                                continue;
                            }
                            context= getBeanContext.substring(getBean.length() + 1, getBeanContext.length() - 2);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        String data = getBeanContext + "," + context;
                        appcontextGetBeanList.add(data);
                    } else if (s.contains(getType) && !s.startsWith("//")) {
                        String classFullPath="";
                        //获取AppContext.getBean(首次出现的位置
                        int start = s.indexOf(getType);
                        //获取反括号的位置
                        int end = s.indexOf(")", start);
                        String getBeanTypeContext = s.substring(start, end + 1);
                        String context = getBeanTypeContext.substring(getType.length(), getBeanTypeContext.length() - 1);
                        context=context.replaceAll(".class","");
                        String path=file.getAbsolutePath();
                        if (imports.get(context) == null) {
                            classFullPath=imports.get("pk")+"."+context;
                        }else {
                            classFullPath=imports.get(context);
                        }
                        String data = getBeanTypeContext + "," + classFullPath;
                        appcontextGetBeansTypeofList.add(data);
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
