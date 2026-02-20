package com.leyi.snack.mapper;

import com.leyi.snack.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();

    List<Category> findLevel1();

    List<Category> findLevel2ByParentId(@Param("parentId") Long parentId);

    Category findLevel1ById(@Param("id") Long id);

    Category findLevel2ById(@Param("id") Long id);

    Category findLevel1ByName(@Param("name") String name);

    Category findLevel2ByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);

    int saveLevel1(Category category);

    int saveLevel2(Category category);

    int updateLevel1(Category category);

    int updateLevel2(Category category);

    int deleteLevel1ById(@Param("id") Long id);

    int deleteLevel2ById(@Param("id") Long id);

    int countLevel2ByParentId(@Param("parentId") Long parentId);

    int countGoodsByCategory2Id(@Param("category2Id") Long category2Id);
}
