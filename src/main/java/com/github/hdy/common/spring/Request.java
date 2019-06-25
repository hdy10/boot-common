package com.github.hdy.common.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 请求类
 *
 * @author 贺大爷
 * @date 2019/2/1
 */
@Getter
@Setter
public class Request implements Serializable {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
}
