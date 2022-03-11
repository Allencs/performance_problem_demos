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
import java.util.Random;
import java.util.concurrent.*;

/**
 * 验证下来结果是，流式查询平均耗时是最短的；
 * fetchSize次之【MySQL fetchSize不生效问题？】，最差的是普通查询；
 *
 * 2022-03-11 18:36:58 [pool-1-thread-1] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:58 [pool-1-thread-1] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - ==> Parameters: 10029(Integer)
 * 2022-03-11 18:36:58 [pool-1-thread-1] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - <==      Total: 10029
 * 2022-03-11 18:36:58 [pool-1-thread-1] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicle --> 耗时： 198
 * 2022-03-11 18:36:58 [pool-1-thread-2] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:58 [pool-1-thread-2] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - ==> Parameters: 10052(Integer)
 * 2022-03-11 18:36:58 [pool-1-thread-2] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicle - <==      Total: 10052
 * 2022-03-11 18:36:58 [pool-1-thread-2] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicle --> 耗时： 129
 * 2022-03-11 18:36:58 [pool-1-thread-3] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:58 [pool-1-thread-3] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - ==> Parameters: 10073(Integer)
 * 2022-03-11 18:36:59 [pool-1-thread-3] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - <==      Total: 10073
 * 2022-03-11 18:36:59 [pool-1-thread-3] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicleWithFlow --> 耗时： 95
 * 2022-03-11 18:36:59 [pool-1-thread-4] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:59 [pool-1-thread-4] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - ==> Parameters: 10082(Integer)
 * 2022-03-11 18:36:59 [pool-1-thread-4] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFlow - <==      Total: 10082
 * 2022-03-11 18:36:59 [pool-1-thread-4] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicleWithFlow --> 耗时： 87
 * 2022-03-11 18:36:59 [pool-1-thread-5] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:59 [pool-1-thread-5] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - ==> Parameters: 10019(Integer)
 * 2022-03-11 18:36:59 [pool-1-thread-5] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - <==      Total: 10019
 * 2022-03-11 18:36:59 [pool-1-thread-5] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicleWithFetchSize --> 耗时： 90
 * 2022-03-11 18:36:59 [pool-1-thread-6] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - ==>  Preparing: select * from vehicle where id <= ?;
 * 2022-03-11 18:36:59 [pool-1-thread-6] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - ==> Parameters: 10021(Integer)
 * 2022-03-11 18:36:59 [pool-1-thread-6] DEBUG com.allen.testMyBatis.VehicleMapper.selectVehicleWithFetchSize - <==      Total: 10021
 * 2022-03-11 18:36:59 [pool-1-thread-6] INFO  com.allen.testMyBatis.TestMyBatis - selectVehicleWithFetchSize --> 耗时： 115
 */

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

     public int getIndex(int range) {
         Random random = new Random();
         return  10000 + random.nextInt(range);
     }

    public static void main(String[] args) throws IOException, InterruptedException {
        Deque<Callable<List<Vehicle>>> arrayDeque = new ArrayDeque<>(20);
        TestMyBatis testMyBatis = new TestMyBatis();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Random random=new Random();
                Vehicle vehicle = testMyBatis.session.selectOne("selectOneVehicle", random.nextInt(50));
                logger.info(vehicle.toString());
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(5000);
        for (int j = 0; j < 10; j++) {
            countDownLatch.countDown();
        }

        Callable<List<Vehicle>> selectVehicle = () -> {
            // 设置随机查询范围，避免MySQL缓存造成影响
            int index = testMyBatis.getIndex(100);
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicle", index);
            logger.info("selectVehicle --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicle);
        arrayDeque.offer(selectVehicle);

        Callable<List<Vehicle>> selectVehicleWithFlow = () -> {
            int index = testMyBatis.getIndex(100);
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicleWithFlow", index);
            logger.info("selectVehicleWithFlow --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicleWithFlow);
        arrayDeque.offer(selectVehicleWithFlow);

        Callable<List<Vehicle>> selectVehicleWithFetchSize = () -> {
            int index = testMyBatis.getIndex(100);
            long startTime=System.currentTimeMillis();
            List<Vehicle> list = testMyBatis.session.selectList("selectVehicleWithFetchSize", index);
            logger.info("selectVehicleWithFetchSize --> 耗时： " + (System.currentTimeMillis() - startTime));
            return list;
        };
        arrayDeque.offer(selectVehicleWithFetchSize);
        arrayDeque.offer(selectVehicleWithFetchSize);

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
