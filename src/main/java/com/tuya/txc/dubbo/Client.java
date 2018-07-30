package com.tuya.txc.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Client {

    public static void main(String args[]) throws Exception {
          ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-client.xml"});
        final OrderService orderService = (OrderService)context.getBean("OrderService");
        final StockService stockService = (StockService)context.getBean("StockService");
        final Calc calcService = (Calc)context.getBean("calcService");

        int previousAmount = stockService.getSum().intValue();
        final String userId = UUID.randomUUID().toString();
        int threadNum = 2;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0;tnum < threadNum;tnum++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0;i < 10;i++) {
                        try {
                            calcService.bussiness(orderService, stockService, userId);
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
        countDownLatch.await();

        int productNumber = orderService.getSum(userId).intValue();
        int currentAmount = stockService.getSum().intValue();
        if (previousAmount== (productNumber + currentAmount)) {
            System.out.println("The result is right.");
        } else {
            System.out.println("previousAmount="+previousAmount);
            System.out.println("productNumber + currentAmount="+(productNumber + currentAmount));
            System.out.println("The result is wrong.");
        }
    }
}
