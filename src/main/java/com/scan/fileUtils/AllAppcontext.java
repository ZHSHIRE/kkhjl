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
 * @create: 2020-11-30 19:38
 * 分别在V5和微服务中扫描AppContext.getBean()**是否为空，生成V5和微服务互相需要代理的bean
 *
 **/
public class AllAppcontext {
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
    //AppContext.getBean()的结果文件路径
    private static final String GetBeanResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\AllNamesUnll.csv";

    //AppContext.getBean()的结果文件路径
    private static final String otherGetBeanResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\V5needPoxyMsNames.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String otherGetBeansTypeofResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\V5needPoxyMsTypes.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String getBeansTypeofResFilePath = "C:\\Users\\ZHSHIRE\\Desktop\\V5\\AllTypesNull.csv";

    private static void Load(File path) {
        if (path.isFile()) {
            findBean(path);
        } else {
            File[] child = path.listFiles();
            for (File f : child) {
                Load(f);
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
                    if(AppContext.getBean(s) == null){
                        appcontextGetBeanList.add(s+",true");
                    }else{   //当前微服务有的，要代理
                        String packege="";
                        try{
                            packege=AppContext.getBean(s).getClass().getName();
                        }catch (Exception e){
                        }
                        otherAppcontextGetBeanList.add(s+",false"+","+packege);
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
                        String type= s.replace(".class", "");
                        if(AppContext.getBeansOfType(Class.forName(type)) == null || AppContext.getBeansOfType(Class.forName(type)).size()<1){
                            appcontextGetBeansTypeofList.add(type+",true");
                        }else{   //当前有的，要代理
                            String packege="";
                            try{
                                packege=AppContext.getBean(s).getClass().getName();
                            }catch (Exception e){

                            }
                            otherAppcontextGetBeansTypeofList.add(type+",false"+","+packege);
                        }
                    }catch (Exception e){
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

    public static boolean exportCsv( List<String> dataList,File file) throws IOException {
        boolean isSucess=false;
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        //文件不存在就创建
        if(!file .exists()){
            //不存在就创建文件
            file.createNewFile();
            //    不存在就创建文件夹
            //    file .mkdir();
        }else{
            file.delete();
            file.createNewFile();
        }
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
            e.printStackTrace();
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("生成文件路径"+   file.getAbsolutePath());
        return isSucess;
    }


    public static void main(String[] args) {
        try {
            appcontextGetBeanList.add("code,isNull");
            appcontextGetBeansTypeofList.add("code,isNull");
            otherAppcontextGetBeanList.add("code,isNull,class");
            otherAppcontextGetBeansTypeofList.add("code,isNull,class");
            File f = new File(projectPath);
            Load(f);
            //去重
            List<String> getBeanList = appcontextGetBeanList.stream().distinct().collect(Collectors.toList());
            exportCsv(getBeanList, new File(GetBeanResFilePath));

            List<String> getBeansOfTypeList = appcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
            exportCsv(getBeansOfTypeList, new File(getBeansTypeofResFilePath));

            //去重
            List<String> getBeanList3 = otherAppcontextGetBeanList.stream().distinct().collect(Collectors.toList());
            exportCsv(getBeanList3, new File(otherGetBeanResFilePath));

            //去重
            List<String> getBeanList4 = otherAppcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
            exportCsv(getBeanList4, new File(otherGetBeansTypeofResFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
