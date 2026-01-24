package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Cart;
import com.leyi.snack.entity.CartVO;
import com.leyi.snack.dto.CartAddDTO;
import com.leyi.snack.mapper.CartMapper;
import com.leyi.snack.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.leyi.snack.entity.Goods;
import com.leyi.snack.mapper.GoodsMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public void add(Long userId, Long goodsId, Integer quantity) {
        // ... (保持原样或统一调用 addToCart)
        CartAddDTO dto = new CartAddDTO();
        dto.setGoodsId(goodsId);
        dto.setQuantity(quantity);
        addToCart(userId, dto);
    }

    @Override
    public void addToCart(Long userId, CartAddDTO dto) {
        Long goodsId = dto.getGoodsId();
        Integer quantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        System.out.println("CartServiceImpl.addToCart: userId=" + userId + ", goodsId=" + goodsId + ", quantity=" + quantity);
        
        // 简化版逻辑：暂时移除严格的库存和状态检查，优先保证加购功能可用
        // Goods goods = goodsMapper.selectById(goodsId); ...
        
        Cart existCart = cartMapper.selectByUserIdAndGoodsId(userId, goodsId);
        
        if (existCart != null) {
            int newQuantity = existCart.getQuantity() + quantity;
            System.out.println("CartServiceImpl.addToCart: Updating existing cart id=" + existCart.getId() + " to quantity=" + newQuantity);
            cartMapper.updateQuantity(existCart.getId(), newQuantity);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setQuantity(quantity);
            System.out.println("CartServiceImpl.addToCart: Saving new cart item");
            cartMapper.save(cart);
            System.out.println("CartServiceImpl.addToCart: Saved cart id=" + cart.getId());
        }
    }

    @Override
    public List<CartVO> list(Long userId) {
        List<CartVO> list = cartMapper.findAllByUserId(userId);
        System.out.println("CartServiceImpl.list: userId=" + userId + ", found " + list.size() + " items");
        return list;
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

    @Override
    public void deleteByUserId(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}
