package com.github.hdy.common.util;

import com.github.hdy.common.exceptions.SpringbootException;

/**
 * 异常辅助工具类
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     *
     * @return 返回异常
     */
    public static SpringbootException mpe(String msg, Throwable t, Object... params) {
        return new SpringbootException(StringUtils.format(msg, params), t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     *
     * @return 返回异常
     */
    public static SpringbootException mpe(String msg, Object... params) {
        return new SpringbootException(StringUtils.format(msg, params));
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     *
     * @return 返回异常
     */
    public static SpringbootException mpe(Throwable t) {
        return new SpringbootException(t);
    }
}
