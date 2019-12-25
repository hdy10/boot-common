package com.github.hdy.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.Set;

/**
 * http请求
 *
 * @author hdy
 * @date 2019/9/25
 */
public class HttpUtil {

    private static String handleParams(String params, String uri) {
        if (Strings.isNull(params)) {
            return "";
        } else {
            if (!params.startsWith("?") && !uri.endsWith("?")) {
                return "?" + params;
            }
            return params;
        }
    }

    public static String get(String uri, String params, Map<String, String> header) {
        params = handleParams(params, uri);
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpGet httpGet = new HttpGet(uri + params);
                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpGet.addHeader(key, header.get(key));
                    }
                }
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String uri, String params, Map<String, String> header) {
        params = handleParams(params, uri);
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpPost httpPost = new HttpPost(uri + params);
                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpPost.addHeader(key, header.get(key));
                    }
                }
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String delete(String uri, String params, Map<String, String> header) {
        params = handleParams(params, uri);
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpDelete httpDelete = new HttpDelete(uri + params);

                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpDelete.addHeader(key, header.get(key));
                    }
                }
                response = client.execute(httpDelete);
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String put(String uri, String params, Map<String, String> header) {
        params = handleParams(params, uri);
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpPut httpPut = new HttpPut(uri + params);

                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpPut.addHeader(key, header.get(key));
                    }
                }
                response = client.execute(httpPut);
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postJson(String uri, String jsonStr, Map<String, String> header) {
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                StringEntity requestEntity = new StringEntity(jsonStr, "utf-8");
                requestEntity.setContentEncoding("UTF-8");
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setEntity(requestEntity);
                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpPost.addHeader(key, header.get(key));
                    }
                } else {
                    httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                }
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                return result;
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
