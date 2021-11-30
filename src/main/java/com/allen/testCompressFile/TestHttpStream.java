package com.allen.testCompressFile;

import com.allen.testHttpComponents.testOkHttp.HttpClientUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestHttpStream {
    private final static String[] URLs = {"https://bing.ioliu.cn/photo/VosgesBioReserve_ZH-CN4762694302?force=download",
//            "https://bing.ioliu.cn/photo/PlaceofRainbows_ZH-CN7878813025?force=download",
//            "https://bing.ioliu.cn/photo/HeligolandSealPup_ZH-CN4217382978?force=download",
            "https://bing.ioliu.cn/photo/PinkMoon_ZH-CN9026483067?force=download",
//            "https://bing.ioliu.cn/photo/PascuaFlorida_ZH-CN7720904158?force=download",
//            "https://bing.ioliu.cn/photo/OntWarbler_ZH-CN7999782156?force=download",
//            "https://bing.ioliu.cn/photo/BodminFrost_ZH-CN4303222848?force=download",
//            "https://bing.ioliu.cn/photo/SantoriniAerial_ZH-CN9367767863?force=download",
//            "https://bing.ioliu.cn/photo/PeruvianRainforest_ZH-CN4066508593?force=download",
            "https://bing.ioliu.cn/photo/RedRobin_ZH-CN4148689161?force=download"};

    private final static OkHttpClient client = HttpClientUtil.CLIENT.getClientInstance();

    public static void downloadWithOkHttp() {
        // 开始
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < URLs.length; i++) {
            try {
                FileOutputStream out = new FileOutputStream("jpgs/" + i + ".jpg");
                Request request = new Request.Builder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .url(URLs[i])
                        .get()
                        .build();
                Call call = client.newCall(request);
                try {
                    InputStream response = call.execute().body().byteStream();
                    int size;
                    byte[] bytes = new byte[4 * 1024 * 1024];
//                    byte[] bytes = new byte[1024];
                    while ((size = response.read(bytes)) != -1) {
                        out.write(bytes, 0, size);
                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("downloadWithOkHttp耗时：" + (System.currentTimeMillis() - startTime));
    }

    public static void downloadWithURLConnection() {
        // 开始
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < URLs.length; i++) {
            try {
                FileOutputStream out = new FileOutputStream("jpgs/" + i + ".jpg");
                URL url = new URL(URLs[i]);
//                byte[] bytes = new byte[4 * 1024 * 1024];
                byte[] bytes = new byte[1024];
                int size = 0;
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                InputStream inputStream = connection.getInputStream();
                while ((size = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, size);
                }
                out.close();
                inputStream.close();
//                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("downloadWithURLConnection耗时：" + (System.currentTimeMillis() - startTime));

    }

    public static void main(String[] args) {
//        downloadWithOkHttp();
        downloadWithURLConnection();
    }
}
