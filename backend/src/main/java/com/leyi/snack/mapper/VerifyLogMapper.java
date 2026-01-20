package com.leyi.snack.mapper;

import com.leyi.snack.entity.VerifyLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerifyLogMapper {
    int save(VerifyLog verifyLog);
}
