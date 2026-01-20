package com.leyi.snack.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Goods {
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String barCode;
    private String imageUrl;
    private String description;
    private Integer isOnSale; // 1:上架, 0:下架
    private LocalDate expireDate;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
