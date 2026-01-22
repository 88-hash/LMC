package com.leyi.snack.mapper;

import com.leyi.snack.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    int batchSave(@Param("items") List<OrderItem> items);
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    // Top10
    List<com.leyi.snack.vo.DashboardFullVO.TopGoods> selectTopSelling(@Param("limit") int limit);
    
    // 品类占比
    List<com.leyi.snack.vo.DashboardFullVO.CategoryShare> selectCategorySalesShare();
}
