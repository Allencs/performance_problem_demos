package com.allen.singleInstance.example;

import com.allen.commons.entity.Person;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

import java.util.concurrent.ConcurrentHashMap;

/**
 * xstream工具类
 *
 */
public class XStreamUtils {

    private static final ConcurrentHashMap<String, XStream> xStream = new ConcurrentHashMap<>();
    private XStreamUtils() {
    }

    /**
     * 通过静态内部类实现单例模式
     */
    private static class SingletonXStream {
        private static final XStream xStream;
        static {
            xStream = new XStream(new Xpp3Driver());
            XStream.setupDefaultSecurity(xStream);
        }
    }

    public static XStream getInstance() {
        return SingletonXStream.xStream;
    }

    /**
     * 每种类型xstream可以分别进行new，然后存在map中
     */
    private static XStream getXStream(Class<?> objName) {
        String key = objName.getName();
        if (xStream.get(key) == null) {
            xStream.put(key, new XStream(new Xpp3Driver()));
        }
        return xStream.get(key);
    }

    public static <T> String toXML(Class<?> reqObjName, T t) {
        XStream xstream = getXStream(reqObjName);
        xstream.useAttributeFor(Person.class, "personid");
        return xstream.toXML(t);
    }


    /**
     * 对象直接转换为XML字符串格式
     *
     * @param t
     * @param <T>
     * @return
     */
//    public static <T> String toXml(T t) {
//        XStream xstream = XStreamUtils.getInstance();
//        xstream.useAttributeFor(Person.class, "personid");
//        return xstream.toXML(t);
//    }

    /**
     * XML直接转化为对象
     *
     * @param xml
     * @param clazz
     * @param <T>
     * @return
     */
//    @SuppressWarnings("unchecked")
//    public static <T> T toBean(String xml, Class<T> clazz) {
//        XStream xstream = XStreamUtils.getInstance();
//        xstream.allowTypeHierarchy(clazz);
//        xstream.processAnnotations(clazz);
//        T obj = (T) xstream.fromXML(xml);
//        return obj;
//    }
    public static void main(String[] args) throws InterruptedException {
//        XStreamUtils xStreamUtils = new XStreamUtils();
        XStreamUtils.getInstance();
        Thread.sleep(1000 * 60);
    }
}
