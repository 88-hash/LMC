package com.leyi.snack.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatsVO {
    private BigDecimal todaySales;
    private Integer todayOrderCount;
    private Integer pendingVerifyCount;
    private Integer lowStockCount;
}