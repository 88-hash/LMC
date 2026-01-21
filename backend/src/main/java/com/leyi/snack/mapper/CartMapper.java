package com.leyi.snack.mapper;

import com.leyi.snack.entity.Cart;
import com.leyi.snack.entity.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {
    // 关联查询返回 VO
    List<CartVO> findAllByUserId(@Param("userId") Long userId);
    
    Cart selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
    
    int save(Cart cart);
    
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    int deleteById(@Param("id") Long id);
    
    int deleteChecked(@Param("userId") Long userId);
    int deleteByUserId(@Param("userId") Long userId);
    int deleteInvalidByUserId(@Param("userId") Long userId);
}
