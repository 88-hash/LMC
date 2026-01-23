package com.leyi.snack.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long orderItemId;
    private String userPhone;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
