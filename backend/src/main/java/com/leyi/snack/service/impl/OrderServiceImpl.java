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
        System.out.println(">>> 开始创建订单, userId=" + userId);

        // 1. 查询购物车 (不再清理，直接查)
        List<CartVO> cartList = cartMapper.findAllByUserId(userId);
        System.out.println(">>> 购物车条数: " + cartList.size());

        if (cartList.isEmpty()) {
            // 如果为空，可能是之前清理过了，这里不再抛异常，而是返回一个提示或者空单号?
            // 不，还是抛个异常提醒前端吧，但在日志里记录清楚
            System.err.println(">>> 错误: 购物车为空");
            throw new RuntimeException("购物车为空，无法下单");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartVO cart : cartList) {
            // 只要有关联到商品(价格不为空)，就参与结算
            if (cart.getPrice() != null) {
                BigDecimal price = cart.getPrice();
                Integer qty = cart.getQuantity();
                
                totalAmount = totalAmount.add(price.multiply(new BigDecimal(qty)));
                
                OrderItem item = new OrderItem();
                item.setGoodsId(cart.getGoodsId());
                item.setGoodsName(cart.getGoodsName());
                item.setGoodsImage(cart.getImageUrl());
                item.setPrice(price);
                item.setQuantity(qty);
                orderItems.add(item);
                
                // 扣库存 (不做严格校验了)
                goodsMapper.reduceStock(cart.getGoodsId(), qty);
            }
        }
        
        if (orderItems.isEmpty()) {
             System.err.println(">>> 错误: 有购物车记录但关联不到商品信息");
             throw new RuntimeException("结算失败: 商品信息异常");
        }

        System.out.println(">>> 订单总金额: " + totalAmount);

        // 2. 生成订单
        String orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setVerifyCode(generateVerifyCode());
        order.setRemark(remark);
        orderMapper.save(order);
        
        // 3. 保存明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.batchSave(orderItems);
        
        // 4. 清空购物车
        cartMapper.deleteByUserId(userId);
        
        System.out.println(">>> 订单创建成功: " + orderNo);
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
