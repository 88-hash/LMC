package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Cart;
import com.leyi.snack.entity.CartVO;
import com.leyi.snack.mapper.CartMapper;
import com.leyi.snack.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void add(Long userId, Long goodsId, Integer quantity) {
        // 1. 检查购物车是否已有该商品
        Cart existCart = cartMapper.selectByUserIdAndGoodsId(userId, goodsId);
        
        if (existCart != null) {
            // 2. 如果有，数量累加
            int newQuantity = existCart.getQuantity() + quantity;
            cartMapper.updateQuantity(existCart.getId(), newQuantity);
        } else {
            // 3. 如果没有，新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setQuantity(quantity);
            cartMapper.save(cart);
        }
    }

    @Override
    public List<CartVO> list(Long userId) {
        return cartMapper.findAllByUserId(userId);
    }

    @Override
    public void update(Long id, Integer quantity) {
        if (quantity <= 0) {
            cartMapper.deleteById(id);
        } else {
            cartMapper.updateQuantity(id, quantity);
        }
    }

    @Override
    public void delete(Long id) {
        cartMapper.deleteById(id);
    }

    @Override
    public void deleteChecked(Long userId) {
        cartMapper.deleteChecked(userId);
    }
}
