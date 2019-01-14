package com.haylion.haylionnetwork.http.exception;

import android.text.TextUtils;

import com.haylion.haylionnetwork.bean.ResBase;

/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:BaseException
 */
public class BaseException extends RuntimeException {

    protected static final String DEFAULT_CODE = "-1";
    protected String code;
    protected String msg;
    protected ResBase resObj;
    protected boolean isDoNothing;


    public BaseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(String code, String msg, ResBase resObj) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.resObj = resObj;
    }

    public BaseException(String msg) {
        super(msg);
        this.code = DEFAULT_CODE;
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResObj(ResBase resObj) {
        this.resObj = resObj;
    }

    public String getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
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

    @Override
    public String toString() {
        return String.format("code=%s, msg=%s", code, msg);
    }
}
