package com.leyi.snack.mapper;

import com.leyi.snack.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (order_item_id, user_phone, rating, content, created_at) " +
            "VALUES (#{orderItemId}, #{userPhone}, #{rating}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT COUNT(*) FROM comment WHERE order_item_id = #{orderItemId}")
    int countByOrderItemId(Long orderItemId);

    @Select("SELECT c.* FROM comment c " +
            "JOIN order_item oi ON c.order_item_id = oi.id " +
            "WHERE oi.goods_id = #{goodsId} " +
            "ORDER BY c.created_at DESC")
    List<Comment> selectByGoodsId(Long goodsId);

    @Select("SELECT c.*, oi.goods_name as goodsName, oi.goods_image as goodsImage " +
            "FROM comment c " +
            "JOIN order_item oi ON c.order_item_id = oi.id " +
            "WHERE c.user_phone = #{userPhone} " +
            "ORDER BY c.created_at DESC")
    List<Map<String, Object>> selectByUserPhoneWithGoodsInfo(String userPhone);

    @Select("SELECT * FROM comment WHERE order_item_id = #{orderItemId}")
    Comment selectByOrderItemId(Long orderItemId);
}
