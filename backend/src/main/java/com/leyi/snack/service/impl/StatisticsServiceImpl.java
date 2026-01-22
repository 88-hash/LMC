package com.leyi.snack.service.impl;

import com.leyi.snack.mapper.GoodsMapper;
import com.leyi.snack.mapper.OrderItemMapper;
import com.leyi.snack.mapper.OrderMapper;
import com.leyi.snack.service.StatisticsService;
import com.leyi.snack.vo.DashboardFullVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        
        // 今日营业额
        BigDecimal todaySales = orderMapper.sumTodaySales();
        overview.setTodaySales(todaySales != null ? todaySales : BigDecimal.ZERO);
        
        // 今日订单量
        Integer todayOrderCount = orderMapper.countTodayOrders();
        overview.setTodayOrderCount(todayOrderCount != null ? todayOrderCount : 0);
        
        // 今日核销数 (用于计算ATV)
        Integer todayVerified = orderMapper.countTodayVerifiedOrders();
        if (todayVerified != null && todayVerified > 0 && todaySales != null) {
            overview.setAverageTicketValue(todaySales.divide(new BigDecimal(todayVerified), 2, RoundingMode.HALF_UP));
        } else {
            overview.setAverageTicketValue(BigDecimal.ZERO);
        }

        // 待核销数
        Integer pending = orderMapper.countPendingOrders();
        overview.setPendingVerify(pending != null ? pending : 0);
        
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
        
        // 5. 品类占比
        List<DashboardFullVO.CategoryShare> shares = orderItemMapper.selectCategorySalesShare();
        vo.setCategoryShare(shares != null ? shares : new ArrayList<>());
        
        // 6. 24小时时段热力
        DashboardFullVO.HourlyStats hourlyStats = new DashboardFullVO.HourlyStats();
        List<String> hours = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        // 初始化 0-23
        int[] hourCounts = new int[24];
        List<Map<String, Object>> hourlyData = orderMapper.selectHourlyOrderCounts();
        if (hourlyData != null) {
            for (Map<String, Object> map : hourlyData) {
                int h = Integer.parseInt(map.get("hour").toString());
                int c = Integer.parseInt(map.get("count").toString());
                if (h >= 0 && h < 24) hourCounts[h] = c;
            }
        }
        for (int i = 0; i < 24; i++) {
            hours.add(i + "点");
            counts.add(hourCounts[i]);
        }
        hourlyStats.setHours(hours);
        hourlyStats.setCounts(counts);
        vo.setHourlyStats(hourlyStats);

        return vo;
    }
}