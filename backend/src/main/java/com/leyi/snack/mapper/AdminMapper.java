package com.leyi.snack.mapper;

import com.leyi.snack.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    /**
     * 根据手机号查询管理员
     */
    Admin selectByPhone(@Param("phone") String phone);
}
