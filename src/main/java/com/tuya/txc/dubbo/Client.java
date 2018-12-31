package com.tuya.txc.dubbo;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client extends AbstractJavaSamplerClient {


    @Override
    public void setupTest(JavaSamplerContext args) {
        System.out.println("args="+args);
        SampleResult sr = new SampleResult();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-client.xml"});
        sr.sampleStart();
        context.start();
    }

    @Override
    public SampleResult runTest(JavaSamplerContext args) {
        System.out.println("args="+args);
        SampleResult sr = new SampleResult();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-client.xml"});
        sr.sampleStart();
        context.start();
        final OrderService orderService = (OrderService)context.getBean("OrderService");
        final StockService stockService = (StockService)context.getBean("StockService");
        final Calc calcService = (Calc)context.getBean("calcService");
        int previousAmount = stockService.getSum().intValue();
        final String userId = "406";
        int previousproductNumber = orderService.getSum(userId).intValue();
        previousAmount = previousAmount + previousproductNumber;
        try {
            calcService.bussiness(orderService, stockService, userId,10);
        } catch (ServiceException e) {
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
        } else {
            sr.setResponseData("The result is wrong ", null);
            sr.setDataType(SampleResult.TEXT);
            sr.setSuccessful(false);
            sr.sampleEnd();
        }

        return  sr;
    }
}
