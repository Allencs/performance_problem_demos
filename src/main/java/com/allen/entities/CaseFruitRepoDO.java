package com.allen.entities;

import java.util.Date;

/**
 * @description:水果仓库表
 * @author: 武士
 * @create: 2022-05-09 10:42
 **/
public class CaseFruitRepoDO {
    private Long id;
    private Integer type_id;
    private String store_id;
    private Float price;
    private String unit;
    private Float count;
    private String from;
    private String to;
    private Integer status;
    private String remark;
    private String create_by;
    private Date create_time;
    private String modify_by;
    private Date modify_time;

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getModify_by() {
        return modify_by;
    }

    public void setModify_by(String modify_by) {
        this.modify_by = modify_by;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType_id() {
        return type_id;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getCount() {
        return count;
    }

    public void setCount(Float count) {
        this.count = count;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getModify_time() {
        return modify_time;
    }

    public void setModify_time(Date modify_time) {
        this.modify_time = modify_time;
    }

    @Override
    public String toString() {
        return "CaseFruitRepoDO{" +
                "id=" + id +
                ", type_id=" + type_id +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", count=" + count +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                ", create_time=" + create_time +
                ", modify_time=" + modify_time +
                '}';
    }
}
