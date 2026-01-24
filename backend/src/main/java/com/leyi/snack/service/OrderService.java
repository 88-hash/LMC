package com.leyi.snack.service;

import com.leyi.snack.entity.Order;
import com.leyi.snack.entity.OrderItem;
import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 创建订单
     * @param userId 用户ID
     * @param remark 备注
     * @return 订单号
     */
    String createOrder(Long userId, String remark);

    /**
     * 我的订单列表
     */
    List<Order> list(Long userId);

    /**
     * 订单详情 (包含商品明细)
     */
    Map<String, Object> detail(Long orderId);

    /**
     * 管理员查询订单列表 (分页 + 筛选)
     */
    Map<String, Object> listAll(Integer status, String keyword, String startDate, String endDate, int page, int size);

    /**
     * 根据订单号查询 (收银台用)
     */
    Order getByOrderNo(String orderNo);

    /**
     * 支付订单
     */
    void pay(String orderNo, String payMethod, Long userId);
}
