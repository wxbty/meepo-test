package com.tuya.txc.dubbo;

import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class CalcService implements Calc {

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void insertOrder(OrderService orderService, String userId, int productNumber) throws ServiceException {
        int productId = new Random().nextInt(1000);
        OrderDO orderDO = new OrderDO(1, userId, productId, productNumber, new Timestamp(new Date().getTime()));
        orderService.createOrder(orderDO);

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void bussiness(OrderService orderService, StockService stockService, String userId) throws ServiceException {
        int productId = new Random().nextInt(1000);
        int productNumber = new Random().nextInt(5) + 1;
        OrderDO orderDO = new OrderDO(1, userId, productId, productNumber, new Timestamp(new Date().getTime()));
        long time1 = System.currentTimeMillis();
        orderService.createOrder(orderDO);
        long time2 = System.currentTimeMillis();
        System.out.println("exe createOrder time = " + (time2 - time1));
        if (new Random().nextInt(100) < 50) {
            throw new ServiceException("error");
        }

        time1 = System.currentTimeMillis();
        stockService.updateStock(orderDO);
        time2 = System.currentTimeMillis();
        System.out.println("exe updateStock time = " + (time2 - time1));
        if (new Random().nextInt(100) < 30) {
            throw new ServiceException("error");
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void bussinessDel(OrderService orderService, StockService stockService, String userId)
            throws ServiceException {
        OrderDO orderDO;
        synchronized (CalcService.class) {
            orderDO = orderService.queryMax();
            orderService.delOrder(orderDO);
        }
        if (new Random().nextInt(100) < 30) {
            throw new ServiceException("error");
        }

        stockService.updateStockDel(orderDO);
        long time3 = System.currentTimeMillis();

        if (new Random().nextInt(100) < 20) {
            throw new ServiceException("error");
        }
    }
}
