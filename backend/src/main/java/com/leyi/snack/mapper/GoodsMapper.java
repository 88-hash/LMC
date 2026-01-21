package com.leyi.snack.mapper;

import com.leyi.snack.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GoodsMapper {
    List<Goods> findAll();
    int save(Goods goods);
    int update(Goods goods);
    int deleteById(@Param("id") Long id);
    
    // 统计库存紧张商品
    Integer countLowStock(@Param("threshold") int threshold);
    Goods selectById(@Param("id") Long id);
    int reduceStock(@Param("id") Long id, @Param("delta") Integer delta);
}
