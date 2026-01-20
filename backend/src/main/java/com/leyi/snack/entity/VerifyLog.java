package com.leyi.snack.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VerifyLog {
    private Long id;
    private Long orderId;
    private Long adminId; // 核销员ID
    private LocalDateTime verifyTime;
}
