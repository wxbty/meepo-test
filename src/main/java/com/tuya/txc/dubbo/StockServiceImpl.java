package com.tuya.txc.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

public class StockServiceImpl implements StockService {

    private static JdbcTemplate jdbcTemplate = null;

    @Transactional(rollbackFor = ServiceException.class)
    public int updateStock(OrderDO orderDO) {
        System.out.println("updateStock is called.");
        try {
            Thread.sleep(new Random().nextInt(7000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int ret = jdbcTemplate.update("update stock set amount = amount - ? where product_id = ?",
                new Object[]{orderDO.getNumber(), orderDO.getProductId()});

        Integer amount = jdbcTemplate.queryForObject("select amount from stock where product_id = ?",
                new Object[]{orderDO.getProductId()}, java.lang.Integer.class);
        if (amount < 0) {
            ret = -1;
            throw new RuntimeException("product："+orderDO.getProductId()+" is not enough.");
        }
        System.out.println("updateStock success.");
        return ret;
    }

    @Transactional(rollbackFor = ServiceException.class)
    public int updateStockDel(OrderDO orderDO) {
        System.out.println("updateStockDel is called.");

        int ret = jdbcTemplate.update("update stock set amount = amount + ? where product_id = ?",
                new Object[]{orderDO.getNumber(), orderDO.getProductId()});

        Integer amount = jdbcTemplate.queryForObject("select amount from stock where product_id = ?",
                new Object[]{orderDO.getProductId()}, java.lang.Integer.class);
        if (amount < 0) {
            throw new RuntimeException("product："+orderDO.getProductId()+" is not enough.");
        }
        System.out.println("updateStockDel success.");
        return ret;
    }
    //获取总库存
    public Integer getSum() {

        System.out.println("getSum is called.");
        Integer sum = jdbcTemplate.queryForObject("select IF(ISNULL(SUM(amount)), 0, SUM(amount)) from stock", java.lang.Integer.class);
        System.out.println("sum:" + sum);
        return sum;
    }

    public static void main(String []args) throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-stock-service.xml"});
        jdbcTemplate = (JdbcTemplate)context.getBean("jdbcTemplate");
        jdbcTemplate.update("delete from  stock where 1=1");
        for (int i = 0;i < 1000;i++) {
            jdbcTemplate.update("insert into stock values(?, ?, ?)", new Object[]{i, new Random().nextInt(100), 100000});
        }
        System.out.println("StockServie is running.");
        System.in.read();
    }
}
