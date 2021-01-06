package com.record.manager;

import com.alibaba.fastjson.JSON;
import com.seeyon.ctp.util.json.JSONUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZHSHIRE
 * @create: 2020-12-04 17:22
 **/
public class RecordMessage {
    private static final String filePath="C:\\Users\\ZHSHIRE\\Desktop\\manageRecord.txt";

    public static void main(String[] args) {
        for (int i=0;i<50;i++){
            record("jfieManager","getALL()","点击佛奥奇偶股价","jfioejdjslfjdslkfjdlsjflkdsjfl");
        }
    }

    /**
     * 将每次请求的managername和参数返回值记录
     * @param serviceName
     * @param methodName
     * @param strArgs
     * @param retValue
     */
    public static synchronized void record(String serviceName, String methodName, String strArgs, String retValue) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("serviceName", serviceName);
            map.put("methodName", methodName);
            map.put("strArgs", strArgs);
            map.put("retValue", retValue);
            appendFIle(filePath,JSONUtil.toJSONString(map));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 网指定文件中追加一行
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

    public Map<String,String> jsonToMap(String json){
        Map maps = (Map) JSON.parse(json);
        return maps;
    }


}
