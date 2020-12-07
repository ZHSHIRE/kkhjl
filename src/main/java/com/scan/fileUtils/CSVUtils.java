package com.scan.fileUtils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZHSHIRE
 * @create: 2020-11-10 16:26
 * CSV文件工具类
 **/
public class CSVUtils {

    /**
     * 读取
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList) throws IOException {
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


    /**
         * 写入
         *
         * @param file csv文件(路径+文件)
         * @return
         */
        public static List<String> importCsv(File file) {
            List<String> dataList = new ArrayList<String>();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = br.readLine()) != null) {
                    dataList.add(line);
                }
            } catch (Exception e) {
            } finally {
                if (br != null) {
                    try {
                        br.close();
                        br = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return dataList;
        }


        /**
         * 测试
         *
         * @param args
         */
        public static void main(String[] args) {
            //exportCsv();
            //importCsv();
        }

        /**
         * CSV写入测试
         *
         * @throws Exception
         */
        public static void exportCsv(List<String> dataList,String path) {
            //List<String> dataList=new ArrayList<String>();
            //dataList.add("number,name,sex");
            //dataList.add("1,张三,男");
            //dataList.add("2,李四,男");
            //dataList.add("3,小红,女");

            boolean isSuccess= false;
            try {
                isSuccess = CSVUtils.exportCsv(new File(path), dataList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isSuccess){
                System.out.println("生成成功");
            }
        }


}
