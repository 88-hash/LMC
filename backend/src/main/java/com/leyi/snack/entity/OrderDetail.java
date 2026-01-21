package com.leyi.snack.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetail {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private String goodsImage;
    private BigDecimal price;
    private Integer quantity;
}

