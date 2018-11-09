package com.tuya.txc.dubbo;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrderDO implements Serializable {

    String userId;
    int productId;
    int number;
    Timestamp gmtCreate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public OrderDO(int id,String userId, int productId, int number, Timestamp gmtCreate) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.number = number;
        this.gmtCreate = gmtCreate;
    }

    public String getUserId() {
        return userId;
    }
    public int getProductId() {
        return productId;
    }
    public int getNumber() {
        return number;
    }
    public Timestamp getGmtCreate() {
        return gmtCreate;
    }
}
