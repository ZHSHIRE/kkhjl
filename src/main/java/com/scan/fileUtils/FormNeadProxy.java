package com.scan.fileUtils;

import com.seeyon.ctp.common.AppContext;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-01 09:43
 * 微服务或者A8扫描
 * 在A8中调用todo方法，结果为微服务需要代理A8中的bean，或者A8代理微服务的bean，和A8和微服务都不存在的bean需要注意
 **/
public class FormNeadProxy {
    //Form中没有的bean
    private static List<String> appcontextGetBeanList = new ArrayList<>();
    //Form中没有的type
    private static List<String> appcontextGetBeansTypeofList = new ArrayList<>();
    //Form中有的bean
    private static List<String> otherAppcontextGetBeanList = new ArrayList<>();
    //Form中有的type
    private static List<String> otherAppcontextGetBeansTypeofList = new ArrayList<>();
    //q读取项目路径
    private static final String projectPath = "C:\\Users\\ZHSHIRE\\Desktop\\工作文件\\老项目拆分微服务\\表单Controller需要代理的bean\\表单微服务中getBean";

    //AppContext.getBean()的结果文件路径
    private static final String FromNeadProxyBean = "C:\\Users\\ZHSHIRE\\Desktop\\Form\\FromNeadProxy.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String FromNeadProxyType = "C:\\Users\\ZHSHIRE\\Desktop\\Form\\V5needPoxyMsTypes.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String FormHaveBean = "C:\\Users\\ZHSHIRE\\Desktop\\Form\\FormHaveBean.csv";

    //AppContext.getBeansTypeof()的结果文件路径
    private static final String FormHaveType = "C:\\Users\\ZHSHIRE\\Desktop\\Form\\FormHaveType.csv";

    static {
        appcontextGetBeanList.add("code,bean");
        appcontextGetBeansTypeofList.add("code,package");
        otherAppcontextGetBeanList.add("code,bean");
        otherAppcontextGetBeansTypeofList.add("code,package");
    }

    /**
     * 项目启动后调用这个方法实现扫描
     * @param args
     */
    public static void todo() {
        File f = new File(projectPath);
        testLoad(f);
        //去重
        List<String> getBeanList = appcontextGetBeanList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeanList, FromNeadProxyBean);

        List<String> getBeansOfTypeList = appcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeansOfTypeList, FromNeadProxyType);

        //去重
        List<String> getBeanList3 = otherAppcontextGetBeanList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeanList3,FormHaveBean);

        //去重
        List<String> getBeanList4 = otherAppcontextGetBeansTypeofList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(getBeanList4,FormHaveType);
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
            if (file.getName().equals("bean.txt")) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
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
            if (file.getName().equals("type.txt")) {
                is = new FileInputStream(file);
                List<String> content = IOUtils.readLines(is, "UTF-8");
                for (int x = 0; x < content.size(); x++) {
                    String s = content.get(x).trim();
                    String type= s.replace(".class", "");
                    try {
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
}
