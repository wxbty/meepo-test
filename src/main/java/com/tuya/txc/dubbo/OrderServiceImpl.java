package com.tuya.txc.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

public class OrderServiceImpl implements OrderService {

    private static JdbcTemplate jdbcTemplate = null;


    @Transactional(rollbackFor = ServiceException.class)
    public int createOrder(OrderDO orderDO) {
        try {
            //设置概率超时
            Thread.sleep(new Random().nextInt(1700));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("createOrder is called.");
        String sql = "insert into orders(user_id,product_id,number,gmt_create) values(?, ?, ?, ?)";
        int ret = jdbcTemplate.update(sql, new Object[]{orderDO.getUserId(), orderDO.getProductId(), orderDO.getNumber(), orderDO.getGmtCreate()});
        try {
            //设置概率超时
            Thread.sleep(new Random().nextInt(1800));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("createOrder success.");
        return ret;
    }


    @Transactional(rollbackFor = ServiceException.class)
    public int delOrder(OrderDO orderDO) {
        System.out.println("delOrder is called.");

        String sql = "delete from orders where user_id =? and product_id =? and num = ?";
        int ret = jdbcTemplate.update(sql, new Object[]{orderDO.getUserId(), orderDO.getProductId(), orderDO.getNumber()});
        System.out.println("delOrder success.");
        return ret;
    }

    public Integer getSum(String userId) {
        System.out.println("getSum is called.");
        Integer sum = jdbcTemplate.queryForObject("select IF(ISNULL(SUM(number)), 0, SUM(number)) from orders where user_id = ?",
                new Object[]{userId}, java.lang.Integer.class);
        System.out.println("sum:" + sum);
        return sum;
    }

    public OrderDO queryRadom()
    {
        System.out.println("queryRadom is called.");
        OrderDO order = jdbcTemplate.queryForObject("select * from orders where id = (select max(id) from orders)",OrderDO.class);
        return order;
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-order-service.xml"});
        context.start();
        jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
        jdbcTemplate.update("delete from orders ");
        System.out.println("OrderService is running.");
        System.in.read();
    }
}
