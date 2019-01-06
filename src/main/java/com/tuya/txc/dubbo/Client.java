package com.tuya.txc.dubbo;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client extends AbstractJavaSamplerClient {

    ClassPathXmlApplicationContext context = null;

    {
        if (context == null) {
            context = new ClassPathXmlApplicationContext(new String[] { "dubbo-client.xml" });
        }
    }

    private OrderService orderService = null;

    private StockService stockService = null;

    private Calc calcService = null;

    @Override
    public void setupTest(JavaSamplerContext args) {
        System.out.println("args=" + args);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        orderService = (OrderService) context.getBean("OrderService");
        stockService = (StockService) context.getBean("StockService");
        calcService = (Calc) context.getBean("calcService");
        context.start();
    }

    @Override
    public SampleResult runTest(JavaSamplerContext args) {

        if (orderService == null)
            orderService = (OrderService) context.getBean("OrderService");
        if (stockService == null)
            stockService = (StockService) context.getBean("StockService");
        if (calcService == null)
            calcService = (Calc) context.getBean("calcService");
        context.start();
        System.out.println("args=" + args);
        SampleResult sr = new SampleResult();
        sr.sampleStart();

        int previousAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        previousAmount = previousAmount + previousproductNumber;
        try {
            calcService.bussiness(orderService, stockService, userId, 10);
        } catch (Exception e) {
            System.out.println("Transaction is rollbacked.");
            e.printStackTrace();
        }
        int productNumber = orderService.getSum(userId).intValue();
        int currentAmount = stockService.getSum().intValue();
        if (previousAmount == (productNumber + currentAmount)) {
            sr.setResponseData("The result is right ", null);
            sr.setDataType(SampleResult.TEXT);
            sr.setSuccessful(true);
            sr.sampleEnd();
            sr.setResponseCode("200");
        } else {
            sr.setResponseData("The result is wrong ", null);
            sr.setDataType(SampleResult.TEXT);
            sr.setSuccessful(false);
            sr.sampleEnd();
            sr.setResponseCode("404");
        }

        return sr;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        OrderService orderService = (OrderService) context.getBean("OrderService");
        StockService stockService = (StockService) context.getBean("StockService");
        Calc calcService = (Calc) context.getBean("calcService");
        context.start();

        int previousAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        previousAmount = previousAmount + previousproductNumber;
        try {
            calcService.bussiness(orderService, stockService, userId, 10);
        } catch (Exception e) {
            System.out.println("Transaction is rollbacked.");
            e.printStackTrace();
        }
        int productNumber = orderService.getSum(userId).intValue();
        int currentAmount = stockService.getSum().intValue();
        if (previousAmount == (productNumber + currentAmount)) {
            System.out.println("The result is right ");

        } else {
            System.out.println("The result is wrong ");
        }
        System.exit(0);
    }
}
