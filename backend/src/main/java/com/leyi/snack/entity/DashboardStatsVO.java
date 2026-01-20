package com.leyi.snack.entity;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsVO {
    private BigDecimal todaySales;      // 今日营业额
    private Integer todayOrderCount;    // 今日订单数
    private Integer lowStockCount;      // 库存预警数
    private Integer pendingOrderCount;  // 待核销数
}
