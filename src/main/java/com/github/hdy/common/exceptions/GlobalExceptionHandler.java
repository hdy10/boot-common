package com.github.hdy.common.exceptions;

import com.github.hdy.common.result.Results;
import com.github.hdy.common.util.Logs;
import com.github.hdy.common.util.Strings;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常
 *
 * @author hdy
 * @date 2019/6/25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Results resolveException(Exception e) {
        String message = e.toString();
        e.printStackTrace();
        if (message.lastIndexOf(":") != -1)
            message = message.substring(0, message.lastIndexOf(":"));
        message = getTraceInfo(e).append(message).toString();
        Logs.error("全局异常：{" + message + "}");
        String msg = e.getMessage();
        int code = 500;
        if (e instanceof MissingServletRequestParameterException) {
            msg = "参数缺失";
        } else if (e instanceof BindException) {
            msg = getBindExceptionMsg((BindException) e);
        } else if (e instanceof NumberFormatException) {
            msg = "格式转换错误";
        } else if (e instanceof HttpMessageNotReadableException) {
            msg = "参数格式有误";
        } else if (e instanceof SQLException) {
            msg = "查询异常";
        } else if (e instanceof NotLoginException) {
            code = 401;
        } else if (e instanceof TokenInvalidException) {
            code = 403;
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            msg = "请求方式错误";
        } else {
            msg = e.getMessage();
        }
        return Results.custom(code, msg, null);
    }

    @SuppressWarnings("unused")
    public static StringBuffer getTraceInfo(Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stacks = e.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            sb.append("class: ").append(stacks[i].getClassName()).append("; method: ").append(stacks[i].getMethodName())
                    .append("; line: ").append(stacks[i].getLineNumber()).append(";  Exception: ");
            break;
        }
        return sb;
    }

    /**
     * Valid 验证失败的异常信息
     *
     * @param e BindException异常信息
     *
     * @return
     */
    public static String getBindExceptionMsg(BindException e) {
        List<String> bindExceptionMsg = new ArrayList<>();
        BindingResult result = e.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                bindExceptionMsg.add(fieldError.getDefaultMessage());
            });
        }
        return Strings.toString(bindExceptionMsg);
    }


}
