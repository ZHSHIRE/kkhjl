package com.scan.fileUtils;

//import com.seeyon.ctp.microservice.bpm.web.fileUtils.CSVUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描spring xml中注入的bean,查看哪些没有实现接口，并将这些没有接口的按照Muti*.properties配置格式bean输出到文本，复制粘贴到配置文件即可
 * @author: ZHSHIRE
 * @create: 2020-12-22 14:53
 **/
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ScanInterface {
    private static int classCount=1;
    //统计表的集合
    private static List<String> tableList=new ArrayList<>();
    private final static String path="C:\\Users\\ZHSHIRE\\Desktop\\工作文件\\老项目拆分微服务\\没实现接口的bean\\noInterfaceBean.txt";

    //
    //@Test
    public void getFilE() {
        tableList.add("classname,tableName,fileName");
        getAllFileName("F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\ctp-microservice-bpm-web\\src\\main\\resources\\cap-core");
        System.out.println("扫描完毕");
        //List<String> resList=tableList.stream().distinct().collect(Collectors.toList());
        //CSVUtils.exportCsv(resList,"C:\\Users\\ZHSHIRE\\Desktop\\工作文件\\老项目拆分微服务\\没实现接口的bean\\noInterfaceBean.csv");
    }

    //public static void main(String[] args) {
    //    tableList.add("classname,tableName,fileName");
    //    getAllFileName("F:\\microservice_8.0sp2test\\ctp-microservice-bpm\\ctp-microservice-bpm-web\\src\\main\\resources\\cap-core");
    //    List<String> resList=tableList.stream().distinct().collect(Collectors.toList());
    //    //CSVUtils.exportCsv(resList,"C:\\Users\\ZHSHIRE\\Desktop\\工作文件\\老项目拆分微服务\\没实现接口的bean\\noInterfaceBean.csv");
    //}

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path) {
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if (tempList[i].getName().endsWith(".xml") && !tempList[i].getName().endsWith("ctp-form.xml")) {
                    txt2String(tempList[i]);
                }
            }
            if (tempList[i].isDirectory()) {
                getAllFileName(tempList[i].getAbsolutePath());
            }
        }
    }

    /**
     * 读取txt文件的内容 * @param file 想要读取的文件对象
     * @param file
     */
    public static void txt2String(File file) {
        final String startClassname = "class=";
        String classname = "";
        //StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String line = null;
            //使用readLine方法，一次读一行
            while ((line = br.readLine()) != null) {
                String s = line.trim();
                if (s.contains(startClassname)) {
                    try {
                        int start  = s.lastIndexOf(startClassname) + startClassname.length();
                        int end = s.indexOf("\"", start + 1);
                        //System.out.println("类名：" + s.substring(start + 1, end));
                        classname = s.substring(start + 1, end);
                        Class aClass=Class.forName(classname);
                        List<Class<?>> superClazzList =new ArrayList<>();
                        List<Class<?>> interfaceList =new ArrayList<>();
                       getAllClazz(aClass,superClazzList,interfaceList);
                        //if("com.seeyon.cap4.unflow.manager.BusinessModuleExportManager4UnflowData".equals(classname)){
                        //    System.out.println();
                        //}
                        Class<?> interfaces[] = aClass.getInterfaces();
                        if(interfaces.length<1 && interfaceList.size()<1){
                            System.out.println("classname："+classname);
                            //结果集
                            //tableList.add(classname+","+file.getName());
                            appendFIle(path,classname+","+"\\");
                        }
                        //for (Class<?> inte : interfaces) {//打印
                        //    System.out.println("classname："+classname+">>>>>"+inte+">>>>filename"+file.getName());
                        //}
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //return result.toString();
    }

    //递归遍历clazz所有父类和接口
    private static void getAllClazz(Class<?> clazz, List<Class<?>> superClazzList,List<Class<?>> interfaceList) {
        if (clazz == null) {
            return;
        }
        if (!superClazzList.contains(clazz)) {
            superClazzList.add(clazz);
            Class<?> superclass = clazz.getSuperclass();
            getAllClazz(superclass, superClazzList,interfaceList);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaceCls : interfaces) {
                interfaceList.add(interfaceCls);
                getAllClazz(interfaceCls, superClazzList,interfaceList);
            }
        }
    }

    /**
     * 将数据追加到文本中
     * @param filePath
     * @param content
     * @throws IOException
     */
    public static void appendFIle(String filePath, String content) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            // 不存在则创建
            f.createNewFile();
        }
        //true,则追加写入text文本
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter (new FileOutputStream (filePath,true),"UTF-8"));
        output.write(content);
        //换行
        output.write("\r\n");
        output.flush();
        output.close();
    }
}
