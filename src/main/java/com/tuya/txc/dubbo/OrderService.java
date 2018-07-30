package com.tuya.txc.dubbo;

/**
 *  订单服务类，实现订单相关操作
 */
public interface OrderService {
    /**
     * 创建商品订单
     *
     * @param orderDO 订单
     * @return 创建订单结果
     */
    int createOrder(OrderDO orderDO);
    int delOrder(OrderDO orderDO);

    /**
     * 获取购买商品数
     *
     * @param userId 用户编号
     * @return 商品数
     */
    Integer getSum(String userId);

    OrderDO queryRadom();
}
