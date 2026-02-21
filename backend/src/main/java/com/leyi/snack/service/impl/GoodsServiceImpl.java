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
    public List<Goods> findByCategoryFilters(Long category1Id, Long category2Id) {
        return goodsMapper.findByCategoryFilters(category1Id, category2Id);
    }

    @Override
    public Goods findById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public void add(Goods goods) {
        normalizeCategory(goods);
        if (goods.getIsOnSale() == null) {
            goods.setIsOnSale(1);
        }
        goodsMapper.save(goods);
    }

    @Override
    public void update(Goods goods) {
        normalizeCategory(goods);
        goodsMapper.update(goods);
    }

    @Override
    public void delete(Long id) {
        goodsMapper.deleteById(id);
    }

    private void normalizeCategory(Goods goods) {
        if (goods == null) {
            throw new RuntimeException("Goods payload cannot be null");
        }
        if (goods.getCategory2Id() == null && goods.getCategoryId() != null) {
            goods.setCategory2Id(goods.getCategoryId());
        }
        if (goods.getCategory2Id() == null || goods.getCategory2Id() <= 0) {
            throw new RuntimeException("Please select a subcategory");
        }
        goods.setCategoryId(goods.getCategory2Id());
    }
}

