package com.leyi.snack.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VerifyLog {
    private Long id;
    private Long orderId;
    private Long adminId;
    private java.time.LocalDateTime verifyTime;
    private String action; // 核销/取消
    private String remark;
}
