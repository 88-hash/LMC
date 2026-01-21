package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Cart;
import com.leyi.snack.entity.CartVO;
import com.leyi.snack.entity.Order;
import com.leyi.snack.entity.OrderItem;
import com.leyi.snack.mapper.CartMapper;
import com.leyi.snack.mapper.GoodsMapper;
import com.leyi.snack.mapper.OrderItemMapper;
import com.leyi.snack.mapper.OrderMapper;
import com.leyi.snack.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, String remark) {
        // 1. 查询购物车中已选中的商品
        // 清理无效购物车项（商品已删除或失效）
        cartMapper.deleteInvalidByUserId(userId);
        // 查询购物车
        List<CartVO> cartList = cartMapper.findAllByUserId(userId);
        List<CartVO> checkedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartVO cart : cartList) {
            if (cart.getIsChecked() == 1 && cart.getPrice() != null) {
                checkedItems.add(cart);
                // 计算总价: price * quantity
                BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        if (checkedItems.isEmpty()) {
            throw new RuntimeException("请先选择商品");
        }

        // 2. 生成订单信息
        String orderNo = generateOrderNo();
        String verifyCode = generateVerifyCode();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 0:待取货
        order.setVerifyCode(verifyCode);
        order.setRemark(remark);
        
        for (CartVO cart : checkedItems) {
            com.leyi.snack.entity.Goods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods == null || goods.getStock() == null || goods.getStock() < cart.getQuantity()) {
                throw new RuntimeException("库存不足");
            }
            goodsMapper.reduceStock(cart.getGoodsId(), cart.getQuantity());
        }

        // 插入主订单
        orderMapper.save(order);

        // 4. 插入订单明细
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cart : checkedItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setGoodsId(cart.getGoodsId());
            item.setGoodsName(cart.getGoodsName());
            item.setGoodsImage(cart.getImageUrl());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            orderItems.add(item);
        }
        orderItemMapper.batchSave(orderItems);

        // 清空购物车
        cartMapper.deleteByUserId(userId);

        return orderNo;
    }

    @Override
    public List<Order> list(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public Map<String, Object> detail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return result;
    }

    // 生成订单号: LY + yyyyMMddHHmmss + 随机3位
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        int random = (int) (Math.random() * 900) + 100;
        return "LY" + dateStr + random;
    }

    // 生成6位数字核销码
    private String generateVerifyCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    @Override
    public Map<String, Object> listAll(Integer status, int page, int size) {
        int offset = (page - 1) * size;
        List<Order> orders = orderMapper.selectAll(status, offset, size);
        Integer total = orderMapper.countAll(status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }
}
