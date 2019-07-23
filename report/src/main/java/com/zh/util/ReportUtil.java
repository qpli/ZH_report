package com.zh.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;
/**
 * Created by lqp on 2019/7/23
 */
public class ReportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReportUtil.class);
    public static final String IMAGE_DIR = "D:/report/upload/";
    public static final String[] IMAGE_FILE_EXT = {"png", "jpg", "jpeg", "bmp"};
    public static String TOUTIAO_DOMAIN = "http://127.0.0.1:8080/";

    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }

    public static boolean isFileAllowed(String fileExt) {
        for (String ext : IMAGE_FILE_EXT) {
            if (fileExt.equals(ext)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 密码强度
     *
     * @return
     * Z = 字母 S = 数字 T = 特殊字符
     */

    /*  一、假定密码字符数范围6-16，除英文数字和字母外的字符都视为特殊字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*){2,}$
    二、假定密码字符数范围6-16，密码字符允许范围为ASCII码表字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[\x00-\x2f\x3A-\x40\x5B-\xFF][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[\x00-\x2F\x3A-\x40\x5B-\xFF][0-9A-Za-z]*){2,}$
    */
    public static String checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "弱";
        }
        if (passwordStr.matches(regexS)) {
            return "弱";
        }
        if (passwordStr.matches(regexT)) {
            return "弱";
        }
        if (passwordStr.matches(regexZT)) {
            return "中";
        }
        if (passwordStr.matches(regexST)) {
            return "中";
        }
        if (passwordStr.matches(regexZS)) {
            return "中";
        }
        if (passwordStr.matches(regexZST)) {
            return "强";
        }
        return passwordStr;

    }

}
