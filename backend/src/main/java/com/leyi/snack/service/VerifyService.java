package com.leyi.snack.service;

import com.leyi.snack.entity.Order;

public interface VerifyService {
    /**
     * 核销查询
     */
    Order queryByVerifyCode(String verifyCode);

    /**
     * 确认核销
     */
    void confirmVerify(String verifyCode, Long adminId);
}
