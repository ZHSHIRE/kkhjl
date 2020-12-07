package com.hejl.client;

import com.seeyon.ctp.util.json.JSONUtil;
import com.squareup.okhttp.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OKClientMain {
    public static  String url = "http://uos.seeyoncd.com/seeyon/seeyon/ajax.do?method=ajaxAction&managerName=%s";
    private static final Log log = LogFactory.getLog(OKClientMain.class);
    private static String path = "C:\\Users\\ZHSHIRE\\Desktop\\manageRecord.txt";

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        OkHttpClient client = new OkHttpClient();
        try{
            InputStream is = new FileInputStream(new File(path));
            List<String> lines =  IOUtils.readLines(is, Charset.forName("UTF-8"));
            for(String s : lines){
                HttpEntity entity = JSONUtil.parseJSONString(s,HttpEntity.class);
                es.submit(new Task(entity,client,createHeader()));
            }
        }catch (Exception e) {
            log.error(e.getLocalizedMessage(),e);
        }
    }
    private static Headers createHeader(){
        return Headers.of("Content-Type","application/x-www-form-urlencoded","Cookie","hostname=192.168.10.115:8083; JSESSIONID=AA60B6D4495F4366478280BC3AD730B2; login_locale=zh_CN; loginPageURL=; avatarImageUrl=6972169676266161699");
    }
}


