package com.github.hdy.common.exceptions;

/**
 * 表示对请求资源的访问被服务器拒绝(无权限等)
 *
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
