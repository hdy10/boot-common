/*
 *  Copyright (c) 2019-2020, 冷冷 (wangiegie@gmail.com).
 *  <p>
 *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 * https://www.gnu.org/licenses/lgpl.html
 *  <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hdy.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.github.hdy.common.exceptions.CheckedException;
import com.github.hdy.common.result.Results;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * Miscellaneous utilities for web applications.
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
@Slf4j
@UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils {
    private final String BASIC_ = "Basic ";
    private final String UNKNOWN = "unknown";

    /**
     * 判断是否ajax请求
     * spring ajax 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     *
     * @return 是否ajax请求
     */
    public boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = ClassUtils.getAnnotation(handlerMethod, ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 读取cookie
     *
     * @param name cookie name
     *
     * @return cookie value
     */
    public String getCookieVal(String name) {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "request from RequestContextHolder is null");
        return getCookieVal(request, name);
    }

    /**
     * 读取cookie
     *
     * @param request HttpServletRequest
     * @param name    cookie name
     *
     * @return cookie value
     */
    public String getCookieVal(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie
     *
     * @param response HttpServletResponse
     * @param key      cookie key
     */
    public void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     *
     * @param response        HttpServletResponse
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAgeInSeconds maxage
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return {HttpServletResponse}
     */
    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 返回json
     *
     * @param response HttpServletResponse
     * @param result   结果对象
     */
    public void renderJson(HttpServletResponse response, Object result) {
        renderJson(response, result, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 返回json
     *
     * @param response    HttpServletResponse
     * @param result      结果对象
     * @param contentType contentType
     */
    public void renderJson(HttpServletResponse response, Object result, String contentType) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);
        try (PrintWriter out = response.getWriter()) {
            out.append(JSONUtil.toJsonStr(result));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取ip
     *
     * @return {String}
     */
    public String getIP() {
        return getIP(WebUtils.getRequest());
    }

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     *
     * @return {String}
     */
    public String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : ip.split(",")[0];
    }

    /**
     * 从request 获取CLIENT_ID
     *
     * @return
     */
    @SneakyThrows
    public String[] getClientId(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BASIC_)) {
            throw new CheckedException("请求头中client信息为空");
        }
        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new CheckedException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new CheckedException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }


    /**
     * post请求
     *
     * @param url    : 请求地址
     * @param params : 参数   格式：{参数1=val&参数2=val}
     */
    public static String URLPost(String url, Object params, HttpServletRequest request) {
        DataOutputStream out = null;
        BufferedReader in = null;
        HttpURLConnection httpCon = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpCon = (HttpURLConnection) realUrl.openConnection();
            httpCon.setConnectTimeout(60000);
            httpCon.setReadTimeout(60000);
            System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
            System.setProperty("sun.net.client.defaultReadTimeout", "60000");
            //设置请求头里面的数据，以下设置用于解决http请求code415的问题
            try {
                JSON.parseObject(params.toString());
                httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            } catch (Exception e) {
                httpCon.setRequestProperty("text/plain", "application/json;charset=UTF-8");
            }
            // 发送POST请求必须设置如下两行
            httpCon.setDoOutput(true);
            httpCon.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpCon.getOutputStream());
            // 发送请求参数
            if (!com.github.hdy.common.util.StringUtils.isNull(params))
                printWriter.write(params.toString());//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            // 定义BufferedReader输入流来读取URL的响应
            //BufferedImage img = ImageIO.read(httpCon.getInputStream());

            in = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！url:{" + url + "}" + e);
            Results results = new Results(202, "网络请求失败,请刷新重试", null);
            return results.toString();
        }// 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                httpCon.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    public static String URLGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Referer", "https://www.baidu.com/s?wd=ip%E6%9F%A5%E8%AF%A2&rsv_spt=1&rsv_iqid=0x8b361eca0000b76d&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=6&rsv_sug1=4&rsv_sug7=100");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            /*for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * post请求
     *
     * @param url    : 请求地址
     * @param params : 参数   格式：{参数1=val&参数2=val}
     */
    public static String URLPostHead(String url, Object params, String... head) {
        DataOutputStream out = null;
        BufferedReader in = null;
        HttpURLConnection httpCon = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpCon = (HttpURLConnection) realUrl.openConnection();
            httpCon.setConnectTimeout(60000);
            httpCon.setReadTimeout(60000);
            System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
            System.setProperty("sun.net.client.defaultReadTimeout", "60000");
            //设置请求头里面的数据，以下设置用于解决http请求code415的问题
            try {
                JSON.parseObject(params.toString());
                httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            } catch (Exception e) {
                httpCon.setRequestProperty("text/plain", "application/json;charset=UTF-8");
            }
            for (String s : head) {
                httpCon.setRequestProperty(s.split("\\.")[0], s.split("\\.")[1]);
            }
            // 发送POST请求必须设置如下两行
            httpCon.setDoOutput(true);
            httpCon.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpCon.getOutputStream());
            // 发送请求参数
            if (!com.github.hdy.common.util.StringUtils.isNull(params))
                printWriter.write(params.toString());//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            // 定义BufferedReader输入流来读取URL的响应
            //BufferedImage img = ImageIO.read(httpCon.getInputStream());

            in = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            Results results = new Results(202, "网络请求失败,请刷新重试", null);
            return results.toString();
        }// 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                httpCon.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }
}

