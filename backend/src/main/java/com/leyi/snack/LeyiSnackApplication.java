package com.leyi.snack;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leyi.snack.mapper")
public class LeyiSnackApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyiSnackApplication.class, args);
    }

}
