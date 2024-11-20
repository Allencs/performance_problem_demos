package com.allen.utils;

import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: allen
 * @Date: 2024/11/19 19:32
 * @Description: 反射执行工具
 **/

@Slf4j
public class ReflectUtil {

    public static Field[] getFields(Class<?> clazz) {
        Class<?> originalClass = clazz;
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();

        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        assert originalClass != null;
        log.info("class: {} fields: {}", originalClass.getName(), fields);
        return fields;
    }

    public static Method getGetMethod(Class<?> objectClass, String fieldName) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, objectClass);
            return descriptor.getReadMethod();
        } catch (Exception e) {
            log.error("get read method error, class: {} field: {}", objectClass.getName(), fieldName, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * java反射bean的set方法
     *
     * @param objectClass
     * @param fieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Method getSetMethod(Class objectClass, String fieldName) {
        try {
            Class[] parameterTypes = new Class[1];
            Field field = objectClass.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuffer sb = new StringBuffer();
            sb.append("set");
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
            Method method = objectClass.getMethod(sb.toString(), parameterTypes);
            return method;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行get方法
     *
     * @param o         执行对象
     * @param fieldName 属性
     */
    public static Object invokeGet(Object o, Method method, String fieldName) {
        try {
            assert method != null;
            return method.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行set方法
     *
     * @param o         执行对象
     * @param fieldName 属性
     * @param value     值
     */
    public static void invokeSet(Object o, Method method, String fieldName, Object value) {
        try {
            assert method != null;
            method.invoke(o, new Object[]{value});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeAllGet(Object o) {
        List<Method> getMethods = new ArrayList<>();
        Field[] fields = getFields(o.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Method method = getGetMethod(o.getClass(), field.getName());
                getMethods.add(method);
            } catch (Exception e) {
            }
        }
        log.info("getMethods: {}", getMethods);
        for (Method getMethod : getMethods) {
            try {
                Object result = invokeGet(o, getMethod, null);
                log.info("class: {} method: {} result: {}", o.getClass().getName(), getMethod.getName(), result.toString());
            } catch (Exception e) {
            }
        }
    }

    public static void invokeAllGet2(Object o) {
        List<Method> getMethods = new ArrayList<>();
        Method[] methods = o.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                if (method.getParameterTypes().length == 0) {
                    getMethods.add(method);
                }
            }
        }
        log.info("class: {} getMethods: {}", o.getClass().getName(), methods);
        for (Method getMethod : getMethods) {
            try {
                Object result = getMethod.invoke(o);
                log.info("class: {} method: {} result: {}", o.getClass().getName(), getMethod.getName(), result.toString());
            } catch (Exception e) {
                log.error("class: {} method: {} ", o.getClass().getName(), getMethod.getName(), e);
            }
        }
    }
}
