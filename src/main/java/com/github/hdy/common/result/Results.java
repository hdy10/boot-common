package com.github.hdy.common.result;

import lombok.Data;

/**
 * 公共返回结果
 *
 * @author hdy
 * @date 2019/6/25
 */
@Data
public class Results {
    private int code;
    private String msg;
    private Object data;

    public Results(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Results success() {
        return new Results(200, "操作成功", null);
    }

    public static Results success(String msg) {
        return new Results(200, msg, null);
    }

    public static Results success(Object data) {
        return new Results(200, null, data);
    }

    public static Results fail() {
        return new Results(500, "操作失败", null);
    }

    public static Results fail(String msg) {
        return new Results(500, msg, null);
    }

    public static Results fail(int code, String msg) {
        return new Results(code, msg, null);
    }

    /**
     * 自定义
     */
    public static Results custom(int code, String msg, Object data) {
        return new Results(code, msg, data);
    }
}
