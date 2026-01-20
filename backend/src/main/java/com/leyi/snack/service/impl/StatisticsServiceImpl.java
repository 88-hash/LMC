package com.leyi.snack.service.impl;

import com.leyi.snack.entity.DashboardStatsVO;
import com.leyi.snack.mapper.GoodsMapper;
import com.leyi.snack.mapper.OrderMapper;
import com.leyi.snack.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        BigDecimal todaySales = orderMapper.sumTodaySales();
        Integer todayOrderCount = orderMapper.countTodayOrders();
        Integer pendingOrderCount = orderMapper.countPendingOrders();
        Integer lowStockCount = goodsMapper.countLowStock(10); // 阈值设为10

        return DashboardStatsVO.builder()
                .todaySales(todaySales)
                .todayOrderCount(todayOrderCount)
                .pendingOrderCount(pendingOrderCount)
                .lowStockCount(lowStockCount)
                .build();
    }
}
