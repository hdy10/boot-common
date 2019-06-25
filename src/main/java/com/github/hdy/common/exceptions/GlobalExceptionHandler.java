package com.github.hdy.common.exceptions;

import com.github.hdy.common.result.Results;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常
 *
 * @author hdy
 * @date 2019/6/25
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LogManager.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(value = Exception.class)
    public Object resolveException(Exception e) {
        String message = e.toString();
        e.printStackTrace();
        if (message.lastIndexOf(":") != -1)
            message = message.substring(0, message.lastIndexOf(":"));
        message = getTraceInfo(e).append(message).toString();
        logger.error("全局异常：{}", message);

        String msg = "";
        int code = 100;
        Results result = new Results(code, msg, null);
        ResponseEntity responseEntity = new ResponseEntity(result, HttpStatus.OK);
        return responseEntity;
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
}
