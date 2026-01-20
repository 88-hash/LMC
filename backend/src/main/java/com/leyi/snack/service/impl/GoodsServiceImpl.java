package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Goods;
import com.leyi.snack.mapper.GoodsMapper;
import com.leyi.snack.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> findAll() {
        return goodsMapper.findAll();
    }

    @Override
    public void add(Goods goods) {
        // 设置默认值
        if (goods.getCategoryId() == null) goods.setCategoryId(0L); // 默认分类
        if (goods.getIsOnSale() == null) goods.setIsOnSale(1); // 默认上架
        goodsMapper.save(goods);
    }

    @Override
    public void update(Goods goods) {
        goodsMapper.update(goods);
    }

    @Override
    public void delete(Long id) {
        goodsMapper.deleteById(id);
    }
}
