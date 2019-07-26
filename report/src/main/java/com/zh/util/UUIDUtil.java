package com.zh.util;

import java.util.UUID;

/**
* @Author:         lisq
* @CreateDate:     2019/7/26 20:03
* @Description:
*/
public class UUIDUtil {
    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }
    
}
