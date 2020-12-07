package com.hejl.client;

import com.squareup.okhttp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class Task implements  Runnable{
    private static final Log log = LogFactory.getLog(Task.class);
    private HttpEntity entity;
    private OkHttpClient client;
    private Headers headers;
    public Task(HttpEntity entity, OkHttpClient client,Headers headers) {
        this.entity = entity;
        this.client = client;
        this.headers = headers;
    }

    @Override
    public void run() {
        try {
            String httpUrl = String.format(OKClientMain.url,entity.getServiceName());
            RequestBody body =  new FormEncodingBuilder().add("managerMethod",entity.getMethodName()).add("arguments",entity.getStrArgs()).build();
            Request request = new Request.Builder().url(httpUrl).post(body).headers(headers).build();
            Response response  =   client.newCall(request).execute();
            System.out.println(response.body().string());
        }catch (Exception e) {
            log.error(e.getLocalizedMessage(),e);
        }
    }
}