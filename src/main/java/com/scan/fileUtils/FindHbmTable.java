package com.scan.fileUtils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-08 11:12
 * 扫描项目中hbm.xml文件总个数并得到类名和表名
 **/
public class FindHbmTable {
    private static int xmlfileCounts=1;
    //统计表的集合
    private static List<String> tableList;


    @Test
    public void getFilE() {
        getAllFileName("F:\\BreakProject\\cap-microservice-core\\cap-microservice-core\\src\\main\\java\\com\\seeyon");
        System.out.println("hbm.xml文件总个数"+xmlfileCounts);
        List<String> resList=tableList.stream().distinct().collect(Collectors.toList());
        CSVUtils.exportCsv(resList,".....csv");
    }

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path) {

        //ArrayList<String> files = new ArrayList<String>();
        boolean flag = false;
        File file = new File(path);
        //clear(file);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if (tempList[i].getName().endsWith(".hbm.xml")) {
                    //log.
                    ++xmlfileCounts;
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
        final String startclassname = "<class";
        final String startTable = "table=";
        String classname = "";
        String tableName = "";
        //StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行

                if (s.contains("<class") || s.contains(startTable)) {
                    int start = s.lastIndexOf(startclassname) + startclassname.length();
                    int end = s.indexOf("\"", start + 1);
                    //System.out.println("类名：" + s.substring(start + 1, end));
                    classname = s.substring(start + 1, end);
                    //System.out.println("类名："+classname);
                    int tablestart = s.lastIndexOf(startTable) + startTable.length();
                    int tablEnd = s.indexOf("\"", tablestart + 1);
                    //表名
                    tableName = s.substring(tablestart + 1, tablEnd);
                    //System.out.println("表名：" + tableName);

                    //结果集
                    tableList.add(classname+","+tableName);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return result.toString();
    }
}
