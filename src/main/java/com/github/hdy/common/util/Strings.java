package com.github.hdy.common.util;

import com.github.hdy.common.entity.TableInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class Strings {

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

    /**
     * 判断参数是否为空
     *
     * @param params 需要判断参数
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

    public static String toString(Object obj) {
        return toString(obj, false);
    }

    public static String toString(Object obj, boolean prettyFormat) {
        StringBuffer s = new StringBuffer();
        if (obj instanceof LocalDate) {
            s.append(toString(((LocalDate) obj).toDate()));
        } else if (obj instanceof LocalTime) {
            s.append(toString(((LocalTime) obj).toString()));
        } else if (obj instanceof DateTime) {
            s.append(toString(((DateTime) obj).toDate()));
        } else if (obj instanceof LocalDateTime) {
            s.append(toString(((LocalDateTime) obj).toDate()));
        } else if (obj instanceof List) {
            List<?> obj_list = (List<?>) obj;
            s.append('[');
            for (int i = 0; i < obj_list.size(); i++) {
                s.append(toString(obj_list.get(i), prettyFormat));
                if (i < obj_list.size() - 1) {
                    s.append(',');
                }
            }
            s.append(']');
        } else if (obj instanceof Map) {
            Map<?, ?> obj_map = (Map<?, ?>) obj;
            List<?> obj_map_keys = Lists.newArrayList(obj_map.keySet());
            s.append('{');
            for (int i = 0; i < obj_map_keys.size(); i++) {
                s.append("'" + obj_map_keys.get(i) + "'" + ":" + toString(obj_map.get(obj_map_keys.get(i)), prettyFormat));
                if (i < obj_map_keys.size() - 1) {
                    s.append(',');
                }
            }
            s.append('}');
        } else if (obj instanceof Object[]) {
            s.append(toString(Lists.newArrayList((Object[]) obj), prettyFormat));
        } else if (obj instanceof String) {
            return obj.toString();
        } else {
            s.append(Jsons.toJSONString(obj, prettyFormat));
        }
        return s.toString();
    }


    /**
     * 安全的进行字符串 format
     *
     * @param target 目标字符串
     * @param params format 参数
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
     * @return 判断结果
     */
    public static boolean isNotColumnName(String str) {
        return !P_IS_COLUMN.matcher(str).matches();
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * 如public baseDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings({"rawtypes"})
    public static Class getClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            Logs.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            Logs.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            Logs.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        return camelToCustom(param, UNDERLINE);
    }

    /**
     * 字符串驼峰转自定义字符标识
     *
     * @param param   需要转换的字符串
     * @param replace 自定义标识符
     * @return
     */
    public static String camelToCustom(String param, char replace) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(replace);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 解析 getMethodName -> propertyName
     *
     * @param getMethodName 需要解析的
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith(IS)) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return Strings.firstToLowerCase(getMethodName);
    }

    /**
     * 字符串下划线转驼峰格式
     *
     * @param param 需要转换的字符串
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
     * @return 首字母大写后的字符串
     */
    public static String capitalize(final String str) {
        return concatCapitalize(null, str);
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
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
     * @return ignore
     */
    public static boolean checkValNull(Object object) {
        return !checkValNotNull(object);
    }

    /**
     * 包含大写字母
     *
     * @param word 待判断字符串
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
     * @return ignore
     */
    public static boolean isCapitalMode(String word) {
        return null != word && word.matches("^[0-9A-Z/_]+$");
    }

    /**
     * 是否为驼峰下划线混合命名
     *
     * @param word 待判断字符串
     * @return ignore
     */
    public static boolean isMixedMode(String word) {
        return matches(".*[A-Z]+.*", word) && matches(".*[/_]+.*", word);
    }


    /**
     * 是否为CharSequence类型
     *
     * @param clazz class
     * @return true 为是 CharSequence 类型
     */
    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence irrespective of case, handling {@code null}. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *) = false
     * StringUtils.contains(*, null) = false
     * StringUtils.contains("", "") = true
     * StringUtils.contains("abc", "") = true
     * StringUtils.contains("abc", "a") = true
     * StringUtils.contains("abc", "z") = false
     * StringUtils.contains("abc", "A") = true
     * StringUtils.contains("abc", "Z") = false
     * </pre>
     */
    public static boolean isContainsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    /**
     * <p>
     * Gets the substring after the last occurrence of a separator. The separator is not returned.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     */
    public static String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses {@link String#indexOf(String)} if possible.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     */
    public static boolean isContains(final CharSequence seq, final CharSequence searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    /**
     * 找到指定的字符串开头位置（忽略大小写）
     * <p>
     * Case in-sensitive find of the first index within a CharSequence.
     * </p>
     * <p/>
     * <pre>
     * Strings.indexOfIgnoreCase(null, *)          = -1
     * Strings.indexOfIgnoreCase(*, null)          = -1
     * Strings.indexOfIgnoreCase("", "")           = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * Strings.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, 0);
    }

    public static String substring(String str, int start) {
        return StringUtils.substring(str, start);
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
     * @return ignore
     */
    public static boolean isBoolean(Class<?> propertyCls) {
        return propertyCls != null && (boolean.class.isAssignableFrom(propertyCls) || Boolean.class.isAssignableFrom(propertyCls));
    }

    /**
     * 第一个首字母小写，之后字符大小写的不变
     * <p>Strings.firstCharToLower( "UserService" )     = userService</p>
     * <p>Strings.firstCharToLower( "UserServiceImpl" ) = userServiceImpl</p>
     *
     * @param rawString 需要处理的字符串
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
     * @return ignore
     */
    public static String prefixToLower(String rawString, int index) {
        String beforeChar = rawString.substring(0, index).toLowerCase();
        String afterChar = rawString.substring(index);
        return beforeChar + afterChar;
    }

    /**
     * 删除字符前缀之后,首字母小写,之后字符大小写的不变
     * <p>Strings.removePrefixAfterPrefixToLower( "isUser", 2 )     = user</p>
     * <p>Strings.removePrefixAfterPrefixToLower( "isUserInfo", 2 ) = userInfo</p>
     *
     * @param rawString 需要处理的字符串
     * @param index     删除多少个字符(从左至右)
     * @return ignore
     */
    public static String removePrefixAfterPrefixToLower(String rawString, int index) {
        return prefixToLower(rawString.substring(index), 1);
    }

    /**
     * 是否是ajax请求
     *
     * @param request
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
     * @return
     */
    public static double getCutNumAfterDot(float number, int scale) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
