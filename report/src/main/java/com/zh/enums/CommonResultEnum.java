package com.zh.enums;

/**
 * Created by lqp on 2019/7/24
 */
public enum CommonResultEnum {
    SUCCESS(1, "成功"),
    ERROR(0, "出错");


    private int state;
    private String stateInfo;

    CommonResultEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static CommonResultEnum stateOf(int index) {
        for (CommonResultEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
}
