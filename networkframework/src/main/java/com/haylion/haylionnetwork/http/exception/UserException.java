package com.haylion.haylionnetwork.http.exception;


import com.haylion.haylionnetwork.bean.ResBase;

/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:自定义异常，需要用户处理的
 */
public class UserException extends BaseException {

    public UserException(String code, String msg, ResBase resObj) {
        super(code, msg, resObj);
    }

    public UserException(String msg) {
        super(msg);
    }
}
