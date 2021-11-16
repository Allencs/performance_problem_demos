package com.allen.testMyBatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestMyBatis {

    private SqlSession session;

    public TestMyBatis() throws IOException {
        this.initialDataSource();
    }

     public void initialDataSource() throws IOException {
         String resource = "mybatis-config.xml";
         InputStream inputStream = Resources.getResourceAsStream(resource);
         SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
         this.session = sqlSessionFactory.openSession();

     }


    public static void main(String[] args) throws IOException, InterruptedException {
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
        int j = 2990;
        for (int i = 0; i < 10; i++) {
            j += 100;
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicle", j);
            System.out.println("--> 耗时： " + (System.currentTimeMillis() - startTime));
        }
//        System.out.println(list.size());
    }
}
