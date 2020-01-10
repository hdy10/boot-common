package com.github.hdy.common.redis.synchronize.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SpEL 表达式计算上下文根对象
 * Created by hdy on 2020/1/10.
 */
@Getter
@AllArgsConstructor
public class ExpressionRootObject {
    private final Object object;
    private final Object[] args;
}
