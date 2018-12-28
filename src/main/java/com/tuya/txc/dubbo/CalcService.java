package com.tuya.txc.dubbo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class CalcService implements Calc {

    @javax.annotation.Resource(name = "jdbcTemplate3")
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void insertOrder(OrderService orderService, String userId, int productNumber) throws ServiceException {
        int productId = new Random().nextInt(1000);
        OrderDO orderDO = new OrderDO(1, userId, productId, productNumber, new Timestamp(new Date().getTime()));
        orderService.createOrder(orderDO);

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void bussiness(OrderService orderService, StockService stockService, String userId, int productNumber) throws ServiceException {
        long time1 = System.currentTimeMillis();
        int productId = new Random().nextInt(1000);
//        int productNumber = new Random().nextInt(5) + 1;
        OrderDO orderDO = new OrderDO(1, userId, productId, productNumber, new Timestamp(new Date().getTime()));
        orderService.createOrder(orderDO);
        long time2 = System.currentTimeMillis();
        System.out.println("exe createOrder time = " + (time2 - time1));
        if (new Random().nextInt(100) < 20) {
            throw new ServiceException("error");
        }

        long time3 = System.currentTimeMillis();
        stockService.updateStock(orderDO);
        long time4 = System.currentTimeMillis();
        System.out.println("exe updateStock time = " + (time4 - time3));
        if (time4 - time1 > 2500)
        {
            System.out.println("t4="+time4+",t1="+time1);
            System.out.println("exe updateBussines time = " + (time4 - time1));
        }
        if (new Random().nextInt(100) < 20) {
            throw new ServiceException("error");
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void bussinessDel(OrderService orderService, StockService stockService, String userId)
            throws ServiceException {
        OrderDO orderDO;
        synchronized (this.getClass()) {
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

    @Override
    public void updateInfo(OrderService orderService, String userId) throws Exception {

//        jdbcTemplate.update("update info set name ='lisi' where id =1");
        int productId = new Random().nextInt(1000);
        OrderDO orderDO = new OrderDO(1, userId, productId, 10, new Timestamp(new Date().getTime()));
        orderService.createOrder(orderDO);

    }
}
