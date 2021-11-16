package com.allen.testMyBatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.*;

public class TestMyBatis {

    private SqlSession session;
    private final static Logger logger = LoggerFactory.getLogger(TestMyBatis.class);
    private final ThreadPoolExecutor executor;

    public TestMyBatis() throws IOException {
        this.initialDataSource();
        this.executor = new ThreadPoolExecutor(10, 10,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());;
    }

     public void initialDataSource() throws IOException {
         String resource = "mybatis-config.xml";
         InputStream inputStream = Resources.getResourceAsStream(resource);
         SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
         this.session = sqlSessionFactory.openSession();

     }


    public static void main(String[] args) throws IOException, InterruptedException {
        Deque<Callable<List<Vehicle>>> arrayDeque = new ArrayDeque<>(10);
        TestMyBatis testMyBatis = new TestMyBatis();
//        CountDownLatch countDownLatch = new CountDownLatch(10);
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                Random random=new Random();
//                Scenario scenario = testMyBatis.session.selectOne("com.allen.testmybatis.scenarioMapper.selectScenario", random.nextInt(10));
//                System.out.println(scenario);
//                try {
//                    countDownLatch.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//
//        Thread.sleep(60000);
//        for (int j = 0; j < 10; j++) {
//            countDownLatch.countDown();
//        }
//        System.out.println("All thread down...");

        Vehicle vehicle = testMyBatis.session.selectOne("selectOneVehicle", 1);
        logger.info(vehicle.toString());

        Callable<List<Vehicle>> selectVehicleWithFlow = () -> {
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicleWithFlow", 5000);
            logger.info("selectVehicleWithFlow --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicleWithFlow);
        arrayDeque.offer(selectVehicleWithFlow);

        Callable<List<Vehicle>> selectVehicleWithFetchSize = () -> {
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicleWithFetchSize", 5000);
            logger.info("selectVehicleWithFetchSize --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicleWithFetchSize);
        arrayDeque.offer(selectVehicleWithFetchSize);

        Callable<List<Vehicle>> selectVehicle = () -> {
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicle", 5000);
            logger.info("selectVehicle --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicle);
        arrayDeque.offer(selectVehicle);

        while (true) {
            Callable task = arrayDeque.poll();
            if (task == null) {
                break;
            }
            Future future = testMyBatis.executor.submit(task);
            try {
                future.get(1, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

        }
        testMyBatis.executor.shutdown();
    }
}
