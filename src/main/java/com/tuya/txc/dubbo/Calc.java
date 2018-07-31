package com.tuya.txc.dubbo;

public interface Calc {


    public void insertOrder(OrderService orderService,String userId,int productNumber) throws ServiceException;
    public void bussiness(OrderService orderService, StockService stockService, String userId) throws ServiceException;
    public void bussinessDel(OrderService orderService, StockService stockService, String userId) throws ServiceException;
}
