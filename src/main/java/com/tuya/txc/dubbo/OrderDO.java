package com.tuya.txc.dubbo;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrderDO implements Serializable {

    String userId;
    int productId;
    int number;
    Timestamp gmtCreate;

    public OrderDO(String userId, int productId, int number, Timestamp gmtCreate) {
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
