package com.allen.aboutBuffer;

import com.allen.testLog.TestLog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class HttpClientTest {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientTest.class);
    static CloseableHttpClient httpClient;
    static PoolingHttpClientConnectionManager cm;
    static HttpGet httpGet = new HttpGet("https://www.perfma.com/");

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
        HttpHost localhost = new HttpHost("locahost", 80);
//        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 1, timeUnit = TimeUnit.MILLISECONDS)
    public static String doGetInBuffer() throws IOException {
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String chunk = null;
        if (entity != null) {
            try (BufferedInputStream inStream = new BufferedInputStream(entity.getContent())) {
                int bisLength = inStream.available();
                byte[] buffer = new byte[bisLength];
                int bytesRead = 0;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    chunk = new String(buffer, 0, bytesRead);
                }
                // do something useful with the response
            }
        }
        logger.info(chunk);
        return chunk;
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 1, timeUnit = TimeUnit.MILLISECONDS)
    public static String doGetNoBuffer() throws IOException {
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }

    public static void main(String[] args) throws IOException, RunnerException {
//        System.out.println(HttpClientTest.doGetInBuffer());
        Options opt = new OptionsBuilder()
                .include(HttpClientTest.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
