package com.haylion.haylionnetwork.http.exception;

import android.text.TextUtils;


import com.haylion.haylionnetwork.bean.ResBase;
import com.haylion.haylionnetwork.http.api.ApiBox;

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

    public static Map<String, String> exComMaps = new HashMap<>();

    static {
        exComMaps.put(ApiBox.FLAG_TOKEN_EXPIRED, "登录过期，请重新登录");
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
