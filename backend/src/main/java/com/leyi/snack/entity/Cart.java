package com.leyi.snack.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long goodsId;
    private Integer quantity;
    private Integer isChecked; // 1:选中, 0:未选中
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
