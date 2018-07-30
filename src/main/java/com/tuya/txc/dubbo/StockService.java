package com.tuya.txc.dubbo;

public interface StockService {

    /**
     * 更新商品库存
     *
     * @param orderDO
     * @return 更新结果
     */
    int updateStock(OrderDO orderDO);

    /**
     * 获取商品总库存
     *
     * @param
     * @return 商品总库存
     */
    Integer getSum();

    int updateStockDel(OrderDO orderDO);
}
