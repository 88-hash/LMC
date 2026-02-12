package com.leyi.snack.common;

import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {
    private Integer code; // 1:成功, 0:失败
    private String message;
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.message = "success";
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(String codeStr, String message) {
        Result<T> result = new Result<>();
        // 这里尝试解析codeStr为Integer，如果不是数字则默认为0
        try {
            result.code = Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            result.code = 0; 
        }
        result.message = message;
        return result;
    }
}
