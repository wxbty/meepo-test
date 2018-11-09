package com.tuya.txc.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
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
        int ret = jdbcTemplate.update(sql,
                new Object[] { orderDO.getUserId(), orderDO.getProductId(), orderDO.getNumber(),
                        orderDO.getGmtCreate() });
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

        String sql = "delete from orders where id =?";
        int ret = jdbcTemplate.update(sql, new Object[] { orderDO.getId() });
        System.out.println("delOrder success.");
        return ret;
    }

    public Integer getSum(String userId) {
        System.out.println("getSum is called.");
        Integer sum = jdbcTemplate
                .queryForObject("select IF(ISNULL(SUM(number)), 0, SUM(number)) from orders where user_id = ?",
                        new Object[] { userId }, java.lang.Integer.class);
        System.out.println("sum:" + sum);
        return sum;
    }

    public OrderDO queryMax() {
        System.out.println("queryMax is called.");
        OrderDO order = jdbcTemplate.queryForObject("select * from orders where id = (select max(id) from orders)",
                new RowMapper<OrderDO>() {

                    @Override
                    public OrderDO mapRow(ResultSet arg0, int arg1) throws SQLException {
                        OrderDO orderDO = new OrderDO(arg0.getInt("id"), arg0.getString("user_id"),
                                arg0.getInt("product_id"), arg0.getInt("number"), arg0.getTimestamp("gmt_create"));
                        return orderDO;
                    }
                });

        return order;
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-order-service.xml" });
        context.start();
        jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
        jdbcTemplate.update("delete from orders ");
        for (int i = 0; i < 1000; i++) {
            String sql = "insert into orders(user_id,product_id,number,gmt_create) values(?, ?, ?, ?)";
            int productId = new Random().nextInt(1000);
            jdbcTemplate.update(sql, new Object[] { "406", productId, 10, new Timestamp(new Date().getTime()) });
        }
        System.out.println("OrderService is running.");
        System.in.read();
    }
}
