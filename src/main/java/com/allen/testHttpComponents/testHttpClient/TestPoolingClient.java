package com.allen.testHttpComponents.testHttpClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

/**
 * CloseableHttpClient线程安全 官方说明：
 * <a href="https://hc.apache.org/httpcomponents-client-5.1.x/migration-guide/preparation.html">...</a>
 *
 */
public class TestPoolingClient {

    static HttpGet httpGet = new HttpGet("https://www.perfma.com");

    static CloseableHttpClient httpClient;
    static PoolingHttpClientConnectionManager cm;

    /**
     *
     */
    static {
        cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
//        HttpHost localhost = new HttpHost("https://www.perfma.com", 80);
//        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    public static void main(String[] args) {
//        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        cm.setMaxTotal(200);
//        cm.setDefaultMaxPerRoute(20);
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setConnectionManager(cm)
//                .build();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
//                CloseableHttpClient httpClient = HttpClients.createDefault();
                try {
                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    System.out.println(response.toString() + " | " + Thread.currentThread().getName());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
