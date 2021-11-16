package com.allen.testHttpComponents.testOkHttp;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TestOkHttp {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //创建请求
                Request request = new Request.Builder()
                        .header("User-Agent", "*****")
                        .addHeader("Accept", "*****")
                        .url("https://www.perfma.com/")
                        .get()
                        .build();
                OkHttpClient client = HttpClientUtil.CLIENT.getClientInstance();

                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
