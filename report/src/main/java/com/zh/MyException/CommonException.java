package com.zh.MyException;

/**
 * Created by lqp on 2019/7/24
 */
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 7676678142982623768L;

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
