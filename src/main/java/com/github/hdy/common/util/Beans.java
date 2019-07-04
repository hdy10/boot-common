package com.github.hdy.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hdy.common.exceptions.SpringbootException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Beans {

    private Beans() {
    }

    public static <T> T newBean(Object obj, Class<T> clazz) {
        return newBean(obj, clazz, null);
    }

    public static <T> T newBean(Object obj, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(obj, clazz, mapper);
    }

    public static <T> T newBean(Map<String, Object> map, Class<T> clazz) {
        return newBean(map, clazz, null);
    }

    public static <T> T newBean(Map<String, Object> map, Class<T> clazz, ObjectMapper mapper) {
        return ObjectMappers.convert(map, clazz, mapper);
    }


    /**
     * 复制对象
     *
     * @param source 被复制对象
     * @param target 复制到目标对象
     */
    public static void copy(Object source, Object target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            throw new SpringbootException(e);
        }
    }

    /**
     * 复制对象(排除为空的对象)
     *
     * @param source 被复制对象
     * @param target 复制到目标对象
     */
    public static void copyNotNull(Object source, Object target) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = source.getClass().getDeclaredFields();
        List<String> emptyList = new ArrayList<>();
        for (Field field : fields) {
            if (Strings.isNull(source.getClass().getMethod("get" + Strings.capitalize(field.getName())).invoke(source))) {
                emptyList.add(field.getName());
            }
        }
        String emptyStrs[] = new String[emptyList.size()];
        for (int i = 0; i < emptyList.size(); i++) {
            emptyStrs[i] = emptyList.get(i);
        }
        try {
            BeanUtils.copyProperties(source, target, emptyStrs);
        } catch (Exception e) {
            throw new SpringbootException(e);
        }
    }
}
