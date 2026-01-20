package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Order;
import com.leyi.snack.entity.VerifyLog;
import com.leyi.snack.mapper.OrderMapper;
import com.leyi.snack.mapper.VerifyLogMapper;
import com.leyi.snack.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyServiceImpl implements VerifyService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private VerifyLogMapper verifyLogMapper;

    @Override
    public Order queryByVerifyCode(String verifyCode) {
        return orderMapper.selectByVerifyCode(verifyCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmVerify(String verifyCode, Long adminId) {
        // 1. 查询订单
        Order order = orderMapper.selectByVerifyCode(verifyCode);
        if (order == null) {
            throw new RuntimeException("核销码无效");
        }

        // 2. 检查状态
        if (order.getStatus() != 0) { // 0:待取货
            throw new RuntimeException("该订单状态不可核销 (状态码: " + order.getStatus() + ")");
        }

        // 3. 更新状态为已完成 (1)
        orderMapper.updateStatus(order.getId(), 1);

        // 4. 记录日志
        VerifyLog log = new VerifyLog();
        log.setOrderId(order.getId());
        log.setAdminId(adminId);
        verifyLogMapper.save(log);
    }
}
