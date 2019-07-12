package com.github.hdy.common.exceptions;

/**
 * 表示未登录
 *
 * @author hdy
 * @date 2019/6/25
 */
public class NotLoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotLoginException() {
        super();
    }

    public NotLoginException(String message) {
        super(message);
    }
}
