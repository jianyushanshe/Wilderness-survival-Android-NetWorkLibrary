package com.jianyushanshe.wildernesssurvivalnetwork.http.exception;

import android.text.TextUtils;


import com.jianyushanshe.wildernesssurvivalnetwork.bean.ResBase;
import com.jianyushanshe.wildernesssurvivalnetwork.http.api.ApiBox;

import java.util.HashMap;
import java.util.Map;


/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:统一处理的异常封装类
 */
public class CommonException extends Exception {


    public static Map<String, String> exApiMaps = new HashMap<>();

    static {
        exApiMaps.put(ApiBox.FLAG_NET_ERROR, "请求超时");
        exApiMaps.put(ApiBox.FLAG_PARSE_ERROR, "解析异常");
        exApiMaps.put(ApiBox.FLAG_PERMISSION_ERROR, "未许可相关权限");
        exApiMaps.put(ApiBox.FLAG_UNKNOWN, "未标记的异常");
    }

    private String code = "";
    private String msg = "";
    private ResBase resObj;
    private boolean isDoNothing;

    public CommonException(Throwable throwable, String code) {
        super(throwable);
        this.code = code;
    }

    public CommonException(UserException e) {
        this(e, e.getCode());
        this.msg = e.getMsg();
        this.resObj = e.getResObj();
    }

    public String getCode() {
        return TextUtils.isEmpty(code) ? "" : code;
    }

    public ResBase getResObj() {
        return resObj;
    }

    public boolean isDoNothing() {
        return isDoNothing;
    }

    public void setDoNothing(boolean doNothing) {
        isDoNothing = doNothing;
    }

    public String getMsg() {
        String reMsg = "";
        if (!TextUtils.isEmpty(msg)) {
            reMsg = msg;
        } else if (TextUtils.isEmpty(msg) && !TextUtils.isEmpty(code)) {
            reMsg = exApiMaps.get(code);
        }
        return reMsg;
    }
}
