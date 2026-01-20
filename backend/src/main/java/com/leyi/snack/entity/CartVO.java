package com.leyi.snack.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    private Long id;
    private Long goodsId;
    private String goodsName;
    private BigDecimal price;
    private String imageUrl;
    private Integer quantity;
    private Integer isChecked;
}
