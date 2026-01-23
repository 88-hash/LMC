package com.leyi.snack.service;

import com.leyi.snack.entity.Comment;
import com.leyi.snack.entity.Order;
import com.leyi.snack.entity.OrderItem;
import com.leyi.snack.mapper.CommentMapper;
import com.leyi.snack.mapper.OrderItemMapper;
import com.leyi.snack.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Transactional
    public void addComment(Long orderItemId, Integer rating, String content, String userPhone) {
        // 1. Check OrderItem
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        if (orderItem == null) {
            throw new RuntimeException("订单商品不存在");
        }

        // 2. Check Order Status
        Order order = orderMapper.selectById(orderItem.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) { // 1: Finished
            throw new RuntimeException("订单未完成，无法评价");
        }

        // 3. Check Duplicate
        if (commentMapper.countByOrderItemId(orderItemId) > 0) {
            throw new RuntimeException("该商品已评价");
        }

        // 4. Save
        Comment comment = new Comment();
        comment.setOrderItemId(orderItemId);
        comment.setUserPhone(userPhone);
        comment.setRating(rating);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);
    }

    public List<Comment> getCommentsByGoodsId(Long goodsId) {
        return commentMapper.selectByGoodsId(goodsId);
    }

    public List<Map<String, Object>> getUserComments(String userPhone) {
        return commentMapper.selectByUserPhoneWithGoodsInfo(userPhone);
    }
}
