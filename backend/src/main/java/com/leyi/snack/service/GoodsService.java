package com.leyi.snack.service;

import com.leyi.snack.entity.Goods;
import java.util.List;

public interface GoodsService {
    List<Goods> findAll();
    List<Goods> findByCategoryFilters(Long category1Id, Long category2Id);
    Goods findById(Long id);
    void add(Goods goods);
    void update(Goods goods);
    void delete(Long id);
}
