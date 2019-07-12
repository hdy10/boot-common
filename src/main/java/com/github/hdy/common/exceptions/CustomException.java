package com.github.hdy.common.exceptions;

/**
 * 自定义异常类
 * Created by hdy on 2019/6/25.
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(Throwable throwable) {
        super(throwable);
    }

    public CustomException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
