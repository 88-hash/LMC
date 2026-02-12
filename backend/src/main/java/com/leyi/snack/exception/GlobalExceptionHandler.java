package com.leyi.snack.exception;

import com.leyi.snack.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        if (message != null && (message.contains("请先登录") || message.contains("无权操作"))) {
            // 401 Unauthorized - 让前端识别并跳转登录
            // 注意：Result.error 默认可能只是返回 code=500，这里我们约定特定错误码
            return Result.error("401", message); 
        }
        return Result.error(message);
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统内部错误: " + e.getMessage());
    }
}
