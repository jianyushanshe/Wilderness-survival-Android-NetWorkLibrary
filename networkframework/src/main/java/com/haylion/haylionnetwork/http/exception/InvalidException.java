package com.haylion.haylionnetwork.http.exception;

import android.text.TextUtils;


import com.haylion.haylionnetwork.bean.ResBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:
 * <p>
 * 自定义异常(针对所有的Respone校验)：
 * 1.强制重新登录
 * 2.校验码错误
 */
public class InvalidException extends BaseException {

    public static final String FLAG_ERROR_RELOGIN = "406";
    public static final String FLAG_ERROR_RESPONCE_CHECK = "2001";

    public static Map<String, String> exComMaps = new HashMap<>();

    static {
        exComMaps.put(FLAG_ERROR_RELOGIN, "登录过期，请重新登录");
        exComMaps.put(FLAG_ERROR_RESPONCE_CHECK, "数据校验失败,请重新操作");
    }

    public InvalidException(String code, String msg, ResBase resObj) {
        super(code, msg, resObj);
    }

    @Override
    public String getMsg() {
        String reMsg = msg;
        if (!TextUtils.isEmpty(code)) {
            reMsg = exComMaps.get(code);
        } else {
            reMsg = "";
        }
        return reMsg;
    }
}
