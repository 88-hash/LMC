package com.leyi.snack.mapper;

import com.leyi.snack.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrdersMapper {
    int save(Orders orders);
    Orders selectById(@Param("id") Long id);
    List<Orders> selectByUserId(@Param("userId") Long userId);
}

