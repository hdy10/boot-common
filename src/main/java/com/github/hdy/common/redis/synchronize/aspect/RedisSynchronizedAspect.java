package com.github.hdy.common.redis.synchronize.aspect;

import com.github.hdy.common.exceptions.CustomException;
import com.github.hdy.common.redis.RedisUtil;
import com.github.hdy.common.redis.synchronize.annotation.RedisSynchronized;
import com.github.hdy.common.redis.synchronize.expression.ExpressionEvaluator;
import com.github.hdy.common.util.Strings;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;

@Aspect
public class RedisSynchronizedAspect {
    @Autowired
    private RedisUtil redisUtil;
    private ExpressionEvaluator<String> evaluator = new ExpressionEvaluator<>();

    @Around("@annotation(redisSynchronized)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point, RedisSynchronized redisSynchronized) {
        String key = getKey(point); // 获取
        long second = redisSynchronized.second();    // 多少秒内不可重复提交
        String msg = redisSynchronized.msg();
        Object o = redisUtil.get(key);
        if (Strings.isNull(o)) {
            redisUtil.set(key, key, second);
            return point.proceed();
        } else {
            throw new CustomException(msg);
        }
    }

    /**
     * SpEL 表达式的解析
     *
     * @param point
     * @return
     */
    public String getKey(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisSynchronized redisSynchronized = method.getAnnotation(RedisSynchronized.class);
        if (point.getArgs() == null) {
            return null;
        }
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(point.getTarget(), point.getTarget().getClass(), ((MethodSignature) point.getSignature()).getMethod(), point.getArgs());
        AnnotatedElementKey methodKey = new AnnotatedElementKey(((MethodSignature) point.getSignature()).getMethod(), point.getTarget().getClass());
        return evaluator.condition(redisSynchronized.key(), methodKey, evaluationContext, String.class);
    }

}

