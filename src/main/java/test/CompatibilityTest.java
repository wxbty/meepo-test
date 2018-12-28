package test;

import com.tuya.txc.dubbo.Calc;
import com.tuya.txc.dubbo.OrderService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertTrue;

/*
 * 不要整个test一起执行，会重复加载xml
 * */
public class CompatibilityTest {

    //测试单sql本地事务
    @Test
    public void testNonTransaction() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "dubbo-client.xml" });
        final OrderService orderService = (OrderService) context.getBean("OrderService");
        final Calc calcService = (Calc) context.getBean("calcService");

        boolean existExceptio = false;

        final String userId = "406";
        try {
            calcService.updateInfo(orderService, userId);
        } catch (Exception e) {
            e.printStackTrace();
            existExceptio = true;
        }
        assertTrue(!existExceptio);
    }

    //test mybatis

    //test cglib

}
