package com.allen.utils;

import java.util.UUID;

public class UuidUtil {

    public static String getStrUuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
