package com.allen.beanCopy;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import org.springframework.objenesis.ObjenesisStd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BeanCopyUtil {
    private BeanCopyUtil() {
    }
    // ObjenesisStd处理无构造方法的类
    private static final ThreadLocal<ObjenesisStd> objenesisStdThreadLocal = ThreadLocal.withInitial(ObjenesisStd::new);
    private static final ConcurrentHashMap<String, BeanCopier> cache = new ConcurrentHashMap<>(16);

    public static <T> T copy(Object source, Class<T> target) {
        return copy(source, objenesisStdThreadLocal.get().newInstance(target));
    }

    public static <T> T copy(Object source, T target) {
        BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
        return target;
    }

    public static <T> List<T> copyList(List<?> sources, Class<T> target) throws Exception {
        if (sources.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<T> list = new ArrayList<>(sources.size());
        ObjenesisStd objenesisStd = objenesisStdThreadLocal.get();
        for (Object source : sources) {
            if (source == null) {
                throw new Exception("no source");
            }
            T newInstance = objenesisStd.newInstance(target);
            BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target);
            beanCopier.copy(source, newInstance, null);
            list.add(newInstance);
        }
        return list;
    }

    public static <T> T mapToBean(Map<?, ?> source, Class<T> target) {
        T bean = objenesisStdThreadLocal.get().newInstance(target);
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(source);
        return bean;
    }

    public static <T> Map<?, ?> beanToMap(T source) {
        return BeanMap.create(source);
    }

    private static <S, T> BeanCopier getCacheBeanCopier(Class<S> source, Class<T> target) {
//        ConcurrentHashMap<Class<?>, BeanCopier> copierConcurrentHashMap = cache.computeIfAbsent(source, aClass -> new ConcurrentHashMap<>(16));
//        return copierConcurrentHashMap.computeIfAbsent(target, aClass -> BeanCopier.create(source, target, false));
        BeanCopier beanCopier = null;
        String key = genKey(source, target);
        if ((beanCopier = cache.get(key)) != null){
            return beanCopier;
        }
        beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        cache.put(key, beanCopier);
        return beanCopier;
    }

    private static String genKey(Class<?> srcClazz, Class<?> destClazz) {
        return srcClazz.getName() + destClazz.getName();
    }
}
