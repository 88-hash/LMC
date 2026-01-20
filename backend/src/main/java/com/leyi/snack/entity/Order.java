package com.leyi.snack.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status; // 0:待取货, 1:已完成, 2:已取消
    private String verifyCode;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
