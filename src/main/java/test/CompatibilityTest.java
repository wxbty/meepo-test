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


/*
* 不要整个test一起执行，会重复加载xml
* */
public class DMLTransactionTest {

    private AtomicInteger total = new AtomicInteger(10000);

    //测试单sql本地事务
    @Test
    public void testInsert() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        final OrderService orderService = (OrderService) context.getBean("OrderService");
        final Calc calcService = (Calc) context.getBean("calcService");

        final String userId = "406";
        final int times = 3;
        int threadNum = 30;
        final AtomicInteger increaseNum = new AtomicInteger(0);
        final AtomicInteger sucessNum = new AtomicInteger(0);
        final AtomicInteger failNum = new AtomicInteger(0);

        int preAmount = orderService.getSum(userId).intValue() + total.get();

        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < times; i++) {
                        int myNum = 0;
                        try {
                            myNum = increaseNum.incrementAndGet();
                            total.getAndAdd(-myNum);
                            calcService.insertOrder(orderService, userId, myNum);
                            sucessNum.incrementAndGet();
                        } catch (Exception e) {
                            failNum.incrementAndGet();
                            total.getAndAdd(myNum);
                            System.out.println("Transaction is rollbacked.num="+myNum);
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
            System.out.println("The sucessNum ="+sucessNum.get());
            System.out.println("The failNum ="+failNum.get());
        } else {
            System.out.println("preAmount=" + preAmount);
            System.out.println("currentAmount=" + currentAmount);
            System.out.println("The result is wrong.");
            System.out.println("The sucessNum ="+sucessNum.get());
            System.out.println("The failNum ="+failNum.get());
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
        int threadNum = 30;
        final int times =3;
        final AtomicInteger seqNo = new AtomicInteger(0);
        final AtomicInteger sucessNum = new AtomicInteger(0);
        final AtomicInteger failNum = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < times; i++) {
                        try {
                            seqNo.incrementAndGet();
                            calcService.bussiness(orderService, stockService, userId,seqNo.get());
                            sucessNum.incrementAndGet();
                        } catch (Exception e) {
                            failNum.incrementAndGet();
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
            System.out.println("The sucessNum ="+sucessNum.get());
            System.out.println("The failNum ="+failNum.get());
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
        final AtomicInteger sucessNum = new AtomicInteger(0);
        final AtomicInteger failNum = new AtomicInteger(0);

        int previousStockAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        int previousAmount = previousStockAmount + previousproductNumber;
        int threadNum = 30;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0; tnum < threadNum; tnum++) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            calcService.bussinessDel(orderService, stockService, userId);
                            sucessNum.incrementAndGet();
                        } catch (Exception e) {
                            failNum.incrementAndGet();
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
            System.out.println("The sucessNum ="+sucessNum.get());
            System.out.println("The failNum ="+failNum.get());
        } else {
            System.out.println("原先Stock库存数量=" + previousStockAmount);
            System.out.println("原先订单包含的产品量=" + previousproductNumber);
            System.out.println("previousAmount=" + previousAmount);
            System.out.println("productNumber + currentAmount=" + (productNumber + currentAmount));
            System.out.println("The result is wrong.");
            System.out.println("最后Stock库存数量=" + currentAmount);
            System.out.println("最后订单包含的产品量=" + productNumber);
        }
        assertTrue(previousAmount == (productNumber + currentAmount));
    }

    //test mybatis

    //test cglib

}
