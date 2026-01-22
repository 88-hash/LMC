package com.leyi.snack.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardFullVO {
    // 1. 顶部概览
    private Overview overview;
    // 2. 趋势图数据
    private Trend trend;
    // 3. 热销 Top10
    private List<TopGoods> topGoods;
    // 4. 风险商品 (库存不足 + 临期)
    private List<RiskGoods> riskGoods;

    @Data
    public static class Overview {
        private BigDecimal todaySales;      // 今日营业额
        private String verifyRate;          // 核销转化率 (已核销/总订单)
        private Integer pendingVerify;      // 待核销数
        private Integer warningCount;       // 风险商品总数
    }

    @Data
    public static class Trend {
        private List<String> dates;         // 日期列表 (X轴)
        private List<BigDecimal> values;    // 营业额列表 (Y轴)
    }

    @Data
    public static class TopGoods {
        private String goodsName;
        private Integer quantity;
        private BigDecimal totalAmount; // 可选
    }

    @Data
    public static class RiskGoods {
        private Long id;
        private String name;
        private Integer stock;
        private String type; // "库存不足" 或 "即将过期"
    }
}