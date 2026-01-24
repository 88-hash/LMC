package com.leyi.snack.mapper;

import com.leyi.snack.entity.Admin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * 根据手机号查询管理员
     */
    Admin selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM admin ORDER BY created_at DESC")
    List<Admin> selectAll();

    @Insert("INSERT INTO admin (name, phone, password, role, created_at) VALUES (#{name}, #{phone}, #{password}, #{role}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Update("UPDATE admin SET name=#{name}, phone=#{phone}, password=#{password}, role=#{role}, updated_at=#{updatedAt} WHERE id=#{id}")
    int update(Admin admin);

    @Delete("DELETE FROM admin WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(Long id);
}
