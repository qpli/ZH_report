package com.zh.util;

/**
 * @Author: lisq
 * @Date: 2019/7/26 11:50
 * @Description:
 */
public class PlatformException extends RuntimeException {
    public PlatformException() {
        super();
    }


    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable e){
        super(message,e);
    }
}
