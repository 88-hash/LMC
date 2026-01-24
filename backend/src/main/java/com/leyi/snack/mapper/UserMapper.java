package com.leyi.snack.mapper;

import com.leyi.snack.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectByPhone(@Param("phone") String phone);
    
    @org.apache.ibatis.annotations.Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    int save(User user);
    int update(User user);
}
