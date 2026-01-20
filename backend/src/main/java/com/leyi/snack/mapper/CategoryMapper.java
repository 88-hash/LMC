package com.leyi.snack.mapper;

import com.leyi.snack.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();
    int save(Category category);
    int update(Category category);
    int deleteById(@Param("id") Long id);
}
