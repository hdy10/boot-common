package com.github.hdy.common.util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class StringUtils {

    /**
     * 空字符
     */
    public static final String EMPTY = "";
    /**
     * 字符串 is
     */
    public static final String IS = "is";
    /**
     * 下划线字符
     */
    public static final char UNDERLINE = '_';
    /**
     * 验证字符串是否是数据库字段
     */
    private static final Pattern P_IS_COLUMN = Pattern.compile("^\\w\\S*[\\w\\d]*$");

    private StringUtils() {
        // to do nothing
    }

    /**
     * 判断参数是否为空
     *
     * @param params 需要判断参数
     *
     * @return 判断结果
     */
    public static boolean isNull(Object... params) {
        if (null == params)
            return true;
        for (Object o : params) {
            if (null == o || o.equals("null") || o.toString().length() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 安全的进行字符串 format
     *
     * @param target 目标字符串
     * @param params format 参数
     *
     * @return format 后的
     */
    public static String format(String target, Object... params) {
        if (target.contains("%s") && ArrayUtils.isNotEmpty(params)) {
            return String.format(target, params);
        }
        return target;
    }

    /**
     * Blob 转为 String 格式
     *
     * @param blob Blob 对象
     *
     * @return 转换后的
     */
    public static String blob2String(Blob blob) {
        if (null != blob) {
            try {
                byte[] returnValue = blob.getBytes(1, (int) blob.length());
                return new String(returnValue, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw ExceptionUtils.mpe("Blob Convert To String Error!");
            }
        }
        return null;
    }

    /**
     * 判断字符串是否为空
     *
     * @param cs 需要判断字符串
     *
     * @return 判断结果
     */
    public static boolean isEmpty(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param cs 需要判断字符串
     *
     * @return 判断结果
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEquals(Object obj, Object... params) {
        for (Object o : params) {
            if (o.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否符合数据库字段的命名
     *
     * @param str 字符串
     *
     * @return 判断结果
     */
    public static boolean isNotColumnName(String str) {
        return !P_IS_COLUMN.matcher(str).matches();
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param param 需要转换的字符串
     *
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 解析 getMethodName -> propertyName
     *
     * @param getMethodName 需要解析的
     *
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith(IS)) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return StringUtils.firstToLowerCase(getMethodName);
    }

    /**
     * 字符串下划线转驼峰格式
     *
     * @param param 需要转换的字符串
     *
     * @return 转换好的字符串
     */
    public static String underlineToCamel(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        String temp = param.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 首字母转换小写
     *
     * @param param 需要转换的字符串
     *
     * @return 转换好的字符串
     */
    public static String firstToLowerCase(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    /**
     * 判断字符串是否为纯大写字母
     *
     * @param str 要匹配的字符串
     *
     * @return
     */
    public static boolean isUpperCase(String str) {
        return matches("^[A-Z]+$", str);
    }

    /**
     * 正则表达式匹配
     *
     * @param regex 正则表达式字符串
     * @param input 要匹配的字符串
     *
     * @return 如果 input 符合 regex 正则表达式格式, 返回true, 否则返回 false;
     */
    public static boolean matches(String regex, String input) {
        if (null == regex || null == input) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * 拼接字符串第二个字符串第一个字母大写
     */
    public static String concatCapitalize(String concatStr, final String str) {
        if (isEmpty(concatStr)) {
            concatStr = EMPTY;
        }
        if (str == null || str.length() == 0) {
            return str;
        }
        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }
        return concatStr + Character.toTitleCase(firstChar) + str.substring(1);
    }

    /**
     * 字符串第一个字母大写
     *
     * @param str 被处理的字符串
     *
     * @return 首字母大写后的字符串
     */
    public static String capitalize(final String str) {
        return concatCapitalize(null, str);
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     *
     * @return ignore
     */
    public static boolean checkValNotNull(Object object) {
        if (object instanceof CharSequence) {
            return isNotEmpty((CharSequence) object);
        }
        return object != null;
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     *
     * @return ignore
     */
    public static boolean checkValNull(Object object) {
        return !checkValNotNull(object);
    }

    /**
     * 包含大写字母
     *
     * @param word 待判断字符串
     *
     * @return ignore
     */
    public static boolean containsUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为大写命名
     *
     * @param word 待判断字符串
     *
     * @return ignore
     */
    public static boolean isCapitalMode(String word) {
        return null != word && word.matches("^[0-9A-Z/_]+$");
    }

    /**
     * 是否为驼峰下划线混合命名
     *
     * @param word 待判断字符串
     *
     * @return ignore
     */
    public static boolean isMixedMode(String word) {
        return matches(".*[A-Z]+.*", word) && matches(".*[/_]+.*", word);
    }


    /**
     * 是否为CharSequence类型
     *
     * @param clazz class
     *
     * @return true 为是 CharSequence 类型
     */
    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    /**
     * 去除boolean类型is开头的字符串
     *
     * @param propertyName 字段名
     * @param propertyType 字段类型
     */
    public static String removeIsPrefixIfBoolean(String propertyName, Class<?> propertyType) {
        if (isBoolean(propertyType) && propertyName.startsWith(IS)) {
            String property = propertyName.replaceFirst(IS, EMPTY);
            if (isEmpty(property)) {
                return propertyName;
            } else {
                String firstCharToLowerStr = firstCharToLower(property);
                return property.equals(firstCharToLowerStr) ? propertyName : firstCharToLowerStr;
            }
        }
        return propertyName;
    }

    /**
     * 是否为Boolean类型(包含普通类型)
     *
     * @param propertyCls ignore
     *
     * @return ignore
     */
    public static boolean isBoolean(Class<?> propertyCls) {
        return propertyCls != null && (boolean.class.isAssignableFrom(propertyCls) || Boolean.class.isAssignableFrom(propertyCls));
    }

    /**
     * 第一个首字母小写，之后字符大小写的不变
     * <p>StringUtils.firstCharToLower( "UserService" )     = userService</p>
     * <p>StringUtils.firstCharToLower( "UserServiceImpl" ) = userServiceImpl</p>
     *
     * @param rawString 需要处理的字符串
     *
     * @return ignore
     */
    public static String firstCharToLower(String rawString) {
        return prefixToLower(rawString, 1);
    }

    /**
     * 前n个首字母小写,之后字符大小写的不变
     *
     * @param rawString 需要处理的字符串
     * @param index     多少个字符(从左至右)
     *
     * @return ignore
     */
    public static String prefixToLower(String rawString, int index) {
        String beforeChar = rawString.substring(0, index).toLowerCase();
        String afterChar = rawString.substring(index);
        return beforeChar + afterChar;
    }

    /**
     * 删除字符前缀之后,首字母小写,之后字符大小写的不变
     * <p>StringUtils.removePrefixAfterPrefixToLower( "isUser", 2 )     = user</p>
     * <p>StringUtils.removePrefixAfterPrefixToLower( "isUserInfo", 2 ) = userInfo</p>
     *
     * @param rawString 需要处理的字符串
     * @param index     删除多少个字符(从左至右)
     *
     * @return ignore
     */
    public static String removePrefixAfterPrefixToLower(String rawString, int index) {
        return prefixToLower(rawString.substring(index), 1);
    }

    /**
     * 是否是ajax请求
     *
     * @param request
     *
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        boolean flag = false;
        String requestType = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 保留2位小数,不四舍五入
     *
     * @param str
     *
     * @return
     */
    public static String FileTo(Double str) {
        String s = str.toString().split("\\.")[1];
        String s1 = str.toString().split("\\.")[0];
        if (s.length() >= 2) {
            return s1 + "." + s.substring(0, 2);
        } else if (s.length() == 1) {
            return s1 + "." + s.substring(0, 1) + "0";
        } else {
            return s1 + ".00";
        }
    }

    /**
     * 判断是否为数字
     *
     * @param str
     *
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 转百分比
     *
     * @param number   double类型的数字
     * @param floatNum 保留几位
     * @param isSymbol 是否要加符号(%)
     *
     * @return
     */
    public static String NumberToRate(float number, int floatNum, boolean isSymbol) {
        StringBuffer pattern = new StringBuffer("0.");
        for (int i = 0; i < floatNum; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.append("%").toString());
        String result = df.format(number);
        return isSymbol ? result : result.substring(0, result.length() - 1);
    }

    /**
     * 小数保留几位
     *
     * @param number 数据
     * @param scale  保留多少位
     *
     * @return
     */
    public static double getCutNumAfterDot(float number, int scale) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
