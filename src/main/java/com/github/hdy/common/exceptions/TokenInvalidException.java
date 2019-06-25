package com.github.hdy.common.exceptions;

/**
 * @author hdy
 * @date 2019/6/25
 */
public class TokenInvalidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenInvalidException() {
        super();
    }

    public TokenInvalidException(String message) {
        super(message);
    }
}
