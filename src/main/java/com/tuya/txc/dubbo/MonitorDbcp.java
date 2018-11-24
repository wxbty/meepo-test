package com.tuya.txc.dubbo;

import org.apache.commons.dbcp2.managed.BasicManagedDataSource;
import org.springframework.beans.factory.InitializingBean;

public class MonitorDbcp implements InitializingBean {

    private BasicManagedDataSource dataSource;

    public void setDataSource(BasicManagedDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {

        new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("active num =" + dataSource.getNumActive());
                    System.out.println("total num =" + dataSource.getMaxTotal());
                }
            }
        }.start();

    }
}
