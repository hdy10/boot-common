package com.github.hdy.common.exceptions;

/**
 * 异常类
 * Created by hdy on 2019/6/25.
 */
public class SpringbootException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SpringbootException(String message) {
        super(message);
    }

    public SpringbootException(Throwable throwable) {
        super(throwable);
    }

    public SpringbootException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
