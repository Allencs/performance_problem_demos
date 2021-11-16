package com.allen.commons.utils;

import java.util.UUID;

public class UuidUtil {

    public static String getStrUuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
