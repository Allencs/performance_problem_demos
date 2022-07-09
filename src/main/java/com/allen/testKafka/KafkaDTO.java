package com.allen.testKafka;

/**
 * @Author: allen
 * @Date: 2022/7/8 10:54 AM
 * @Description:
 */
public class KafkaDTO {

    public String id;

    public String data;

    public KafkaDTO(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
