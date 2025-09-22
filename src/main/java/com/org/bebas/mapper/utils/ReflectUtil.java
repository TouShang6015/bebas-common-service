package com.org.bebas.mapper.utils;


import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;

/**
 * 反射工具类增强
 *
 * @author WuHao
 * @since 2023/7/26 8:46
 */
public class ReflectUtil {

    /**
     * 获得类中泛型类型字节码class
     *
     * @param index
     * @return
     */
    public static <T> Class<T> getGenericsClass(Class<?> cls, Integer index) {
        Assert.notNull(cls);
        try {
            return (Class<T>) ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments()[index];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
