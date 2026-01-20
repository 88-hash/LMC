package com.leyi.snack.mapper;

import com.leyi.snack.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectByPhone(@Param("phone") String phone);
    int save(User user);
    int update(User user);
}
