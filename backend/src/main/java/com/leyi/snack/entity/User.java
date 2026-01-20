package com.leyi.snack.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String name;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
