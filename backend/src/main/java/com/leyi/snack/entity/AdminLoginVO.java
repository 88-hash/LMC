package com.leyi.snack.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private String role;
    private String token;
}
