package com.allen.testRedis.jackson;

import com.allen.commons.entity.CaseFruitRepoDO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @Author: allen
 * @Date: 2022/5/31 16:11
 * @Description:
 **/
public class TestJackson {

    private final static String str = "{\"id\":1,\"type_id\":1,\"store_id\":\"KC202205181000001\",\"price\":10.0,\"unit\":\"KG\",\"count\":100.0,\"from\":\"上海\",\"to\":\"北京\",\"status\":0,\"remark\":\"1车苹果\",\"create_by\":\"武士\",\"create_time\":\"2022-05-02 04:40:50\",\"modify_by\":\"艾伦\",\"modify_time\":\"2022-05-19 01:10:30\"}";
//    private final static ObjectMapper om;

    static  {
//        om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        // 日期序列化处理(禁止转成时间戳)
//        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        om.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        try {
            setFruit();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFruit() throws ParseException {
        CaseFruitRepoDO fruitRepoDO = new CaseFruitRepoDO();
        fruitRepoDO.setCount(100F);
        fruitRepoDO.setType_id(1);
        fruitRepoDO.setTo("北京");
        fruitRepoDO.setUnit("KG");
        fruitRepoDO.setRemark("1车苹果");
        fruitRepoDO.setCreate_by("武士");
        fruitRepoDO.setModify_by("艾伦");
        fruitRepoDO.setId(1L);
        fruitRepoDO.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-02 04:40:50"));
        fruitRepoDO.setFrom("上海");
        fruitRepoDO.setPrice(10F);
        fruitRepoDO.setStatus(0);
        fruitRepoDO.setStore_id("KC202205181000001");
        fruitRepoDO.setModify_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-19 01:10:30"));
    }

    public static void main(String[] args) throws JsonProcessingException {
//        Jedis jedis = new Jedis("10.10.220.15", 6379);
//        jedis.auth("Perfma888.");

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("10.10.220.15", 6379);
                    jedis.auth("Perfma888.");

                    while (true) {
                        String strFruit = jedis.get("fruit:detail:cache:1");
                        CaseFruitRepoDO fruit = null;
                        try {
                            ObjectMapper om = new ObjectMapper();
                            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                            // 日期序列化处理(禁止转成时间戳)
                            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                            om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                            om.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            fruit = om.readValue(strFruit, CaseFruitRepoDO.class);
                        } catch (Exception e) {
                            e.printStackTrace();
//                            break;
                        }
                        System.out.println(fruit);
                    }
                }
            }).start();
        }

//        for (int i = 0; i < 100000; i++) {
//            String strFruit = jedis.get("fruit:detail:cache:1");
//            CaseFruitRepoDO fruit = om.readValue(strFruit, CaseFruitRepoDO.class);
//            System.out.println(fruit);
//        }
//        jedis.close();
//            jedis.set("fruit:detail:cache:1", om.writeValueAsString(fruitRepoDO));



    }
}
