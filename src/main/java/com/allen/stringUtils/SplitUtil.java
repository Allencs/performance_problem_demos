package com.allen.stringUtils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: allen
 * @Date: 2022/10/13 17:04
 * @Description: 自定义split工具
 */
public class SplitUtil {
    /**
     * 自定义分割函数，返回第一个
     *
     * @param str   待分割的字符串
     * @param delim 分隔符
     * @return 分割后的第一个字符串
     */
    public static String splitFirst(final String str, final String delim) {
        if (null == str || StringUtils.isEmpty(delim)) {
            return str;
        }

        int index = str.indexOf(delim);
        if (index < 0) {
            return str;
        }
        if (index == 0) {
            // 一开始就是分隔符，返回空串
            return "";
        }

        return str.substring(0, index);
    }

    /**
     * 自定义分割函数，返回全部
     *
     * @param str   待分割的字符串
     * @param delim 分隔符
     * @return 分割后的返回结果
     */
    public static List<String> split(String str, final String delim) {
        if (null == str) {
            return new ArrayList<>(0);
        }

        if (StringUtils.isEmpty(delim)) {
            List<String> result = new ArrayList<>(1);
            result.add(str);

            return result;
        }

        final List<String> stringList = new ArrayList<>();
        while (true) {
            int index = str.indexOf(delim);
            if (index < 0) {
                stringList.add(str);
                break;
            }
            stringList.add(str.substring(0, index));
            str = str.substring(index + delim.length());
        }
        return stringList;
    }
}
