package test;

import com.tuya.txc.dubbo.Calc;
import com.tuya.txc.dubbo.OrderService;
import com.tuya.txc.dubbo.StockService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionTest {

    private AtomicInteger total = new AtomicInteger(200);

    //测试单sql本地事务
    @Test
    public void testInsert() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        final OrderService orderService = (OrderService) context.getBean("OrderService");
        final Calc calcService = (Calc) context.getBean("calcService");

        final String userId = "406";
        final int increaseNum = 10;
        final int times = 13;
        int threadNum = 20;

        int preAmount = orderService.getSum(userId).intValue() + total.get();

        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < times; i++) {
                        try {
                            total.getAndAdd(-increaseNum);
                            calcService.insertOrder(orderService, userId, increaseNum);
                        } catch (Exception e) {
                            total.getAndAdd(increaseNum);
                            System.out.println("Transaction is rollbacked.");
                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                }
            };
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int currentAmount = orderService.getSum(userId).intValue() + total.get();
        if (currentAmount == preAmount) {
            System.out.println("The result is right.");
        } else {
            System.out.println("preAmount=" + preAmount);
            System.out.println("currentAmount=" + currentAmount);
            System.out.println("The result is wrong.");
        }
        assertTrue(currentAmount == preAmount);
    }

    //插入订单，减少库存
    @Test
    public void testInsertAndUpdate() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        final OrderService orderService = (OrderService) context.getBean("OrderService");
        final StockService stockService = (StockService) context.getBean("StockService");
        final Calc calcService = (Calc) context.getBean("calcService");

        int previousAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        previousAmount = previousAmount + previousproductNumber;
        int threadNum = 20;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            calcService.bussiness(orderService, stockService, userId);
                        } catch (Exception e) {
                            System.out.println("Transaction is rollbacked.");
                            //                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                }
            };
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int productNumber = orderService.getSum(userId).intValue();
        int currentAmount = stockService.getSum().intValue();
        if (previousAmount == (productNumber + currentAmount)) {
            System.out.println("The result is right.");
        } else {
            System.out.println("previousAmount=" + previousAmount);
            System.out.println("productNumber + currentAmount=" + (productNumber + currentAmount));
            System.out.println("The result is wrong.");
        }
        assertTrue(previousAmount == (productNumber + currentAmount));
    }

    /*
    * 执行delete之前，确保orders表有足够的记录
    * */
    @Test
    public void testDeleteAndUpdate() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        final OrderService orderService = (OrderService) context.getBean("OrderService");
        final StockService stockService = (StockService) context.getBean("StockService");
        final Calc calcService = (Calc) context.getBean("calcService");

        int previousStockAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        int previousAmount = previousStockAmount + previousproductNumber;
        int threadNum = 17;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < 12; i++) {
                        try {
                            calcService.bussinessDel(orderService, stockService, userId);
                        } catch (Exception e) {
                            System.out.println("Transaction is rollbacked.");
                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                }
            };
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int productNumber = orderService.getSum(userId).intValue();
        int currentAmount = stockService.getSum().intValue();
        if (previousAmount == (productNumber + currentAmount)) {
            System.out.println("The result is right.");
        } else {
            System.out.println("原先Stock库存数量="+previousStockAmount);
            System.out.println("原先订单包含的产品量="+previousproductNumber);
            System.out.println("previousAmount=" + previousAmount);
            System.out.println("productNumber + currentAmount=" + (productNumber + currentAmount));
            System.out.println("The result is wrong.");
            System.out.println("最后Stock库存数量="+currentAmount);
            System.out.println("最后订单包含的产品量="+productNumber);
        }
        assertTrue(previousAmount == (productNumber + currentAmount));
    }

    //test mybatis

    //test cglib

}
