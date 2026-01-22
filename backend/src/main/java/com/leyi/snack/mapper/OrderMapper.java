package com.leyi.snack.mapper;

import com.leyi.snack.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface OrderMapper {
    int save(Order order);
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    Order selectByVerifyCode(@Param("verifyCode") String verifyCode);
    List<Order> selectByUserId(@Param("userId") Long userId);
    Order selectById(@Param("id") Long id);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // --- 管理员统计接口 ---
    BigDecimal sumTodaySales();
    Integer countTodayOrders();
    Integer countPendingOrders();

    // --- 管理员订单列表 (带分页) ---
    List<Order> selectAll(@Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    Integer countAll(@Param("status") Integer status);
    
    // 新增统计方法
    List<java.util.Map<String, Object>> selectSalesTrend(@Param("days") int days);
    Integer countTotalOrders();
    Integer countVerifiedOrders();
}
