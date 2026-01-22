package com.leyi.snack.service.impl;

import com.leyi.snack.mapper.GoodsMapper;
import com.leyi.snack.mapper.OrderItemMapper;
import com.leyi.snack.mapper.OrderMapper;
import com.leyi.snack.service.StatisticsService;
import com.leyi.snack.vo.DashboardFullVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public DashboardFullVO getDashboardStats() {
        DashboardFullVO vo = new DashboardFullVO();

        // 1. 概览数据
        DashboardFullVO.Overview overview = new DashboardFullVO.Overview();
        BigDecimal todaySales = orderMapper.sumTodaySales();
        overview.setTodaySales(todaySales != null ? todaySales : BigDecimal.ZERO);

        Integer totalOrders = orderMapper.countTotalOrders();
        Integer verifiedOrders = orderMapper.countVerifiedOrders();
        if (totalOrders != null && totalOrders > 0) {
            double rate = (double) verifiedOrders / totalOrders * 100;
            overview.setVerifyRate(String.format("%.1f%%", rate));
        } else {
            overview.setVerifyRate("0%");
        }

        Integer pending = orderMapper.countPendingOrders();
        overview.setPendingVerify(pending != null ? pending : 0);

        // 风险商品数 = 低库存 + 临期
        Integer lowStockCount = goodsMapper.countLowStock(10);
        overview.setWarningCount(lowStockCount != null ? lowStockCount : 0); // 暂时只统计库存
        
        vo.setOverview(overview);

        // 2. 趋势数据 (近7天)
        List<Map<String, Object>> trendData = orderMapper.selectSalesTrend(7);
        DashboardFullVO.Trend trend = new DashboardFullVO.Trend();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        
        if (trendData != null) {
            for (Map<String, Object> map : trendData) {
                dates.add(map.get("date").toString());
                values.add((BigDecimal) map.get("amount"));
            }
        }
        trend.setDates(dates);
        trend.setValues(values);
        vo.setTrend(trend);

        // 3. Top10 商品
        List<DashboardFullVO.TopGoods> topGoods = orderItemMapper.selectTopSelling(10);
        vo.setTopGoods(topGoods != null ? topGoods : new ArrayList<>());

        // 4. 风险商品列表
        List<DashboardFullVO.RiskGoods> riskList = new ArrayList<>();
        List<DashboardFullVO.RiskGoods> lowStocks = goodsMapper.selectLowStockGoodsList(10);
        if (lowStocks != null) riskList.addAll(lowStocks);
        
        List<DashboardFullVO.RiskGoods> expiring = goodsMapper.selectExpiringGoods(30);
        if (expiring != null) riskList.addAll(expiring);
        
        vo.setRiskGoods(riskList);

        return vo;
    }
}