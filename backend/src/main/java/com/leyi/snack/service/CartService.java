package com.leyi.snack.service;

import com.leyi.snack.entity.CartVO;
import com.leyi.snack.dto.CartAddDTO;
import java.util.List;

public interface CartService {
    void add(Long userId, Long goodsId, Integer quantity);
    void addToCart(Long userId, CartAddDTO dto);
    List<CartVO> list(Long userId);
    void update(Long id, Integer quantity);
    void delete(Long id);
    void deleteChecked(Long userId);
    void deleteByUserId(Long userId);
}
