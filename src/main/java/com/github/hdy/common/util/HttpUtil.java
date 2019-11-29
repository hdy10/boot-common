package com.github.hdy.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;
import java.util.Set;

/**
 * http请求
 *
 * @author hdy
 * @date 2019/9/25
 */
public class HttpUtil {

    public static String get(String uri, String params, Map<String, String> header) {
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

    public static String postJson(String uri, JSONObject params, Map<String, String> header) {
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(params),
                        ContentType.create("text/json", "UTF-8")));
                client = HttpClients.createDefault();
                if (header != null && header.size() > 0) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        httpPost.addHeader(key, header.get(key));
                    }
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
