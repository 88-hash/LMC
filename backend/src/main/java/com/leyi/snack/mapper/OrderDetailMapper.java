package com.leyi.snack.mapper;

import com.leyi.snack.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    int batchSave(@Param("list") List<OrderDetail> list);
    List<OrderDetail> selectByOrderId(@Param("orderId") Long orderId);
}

