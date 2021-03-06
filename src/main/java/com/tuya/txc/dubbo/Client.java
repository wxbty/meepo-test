package com.tuya.txc.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {


    public static void main(String args[]) throws Exception {
          ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-client.xml"});
        final OrderService orderService = (OrderService)context.getBean("OrderService");
        final StockService stockService = (StockService)context.getBean("StockService");
        final Calc calcService = (Calc)context.getBean("calcService");

        int previousAmount = 0;
//        int previousAmount = stockService.getSum().intValue();
        final String userId = "406";
        int threadNum = 1;
        final AtomicInteger seqNo = new AtomicInteger(1);
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int tnum = 0;tnum < threadNum;tnum++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0;i < 1;i++) {
                        try {
                            calcService.updateInfo(orderService,userId);
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

        System.exit(0);
    }

       public void printTrack(){
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            if(st==null){
//                logger.info("无堆栈...");
                return;
            }
            StringBuffer sbf =new StringBuffer();
            for(StackTraceElement e:st){
                if(sbf.length()>0){
                    sbf.append(" <- ");
                    sbf.append(System.getProperty("line.separator"));
                }
                sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                        ,e.getClassName()
                        ,e.getMethodName()
                        ,e.getLineNumber()));
            }
//            logger.info(sbf.toString());
        }
}
