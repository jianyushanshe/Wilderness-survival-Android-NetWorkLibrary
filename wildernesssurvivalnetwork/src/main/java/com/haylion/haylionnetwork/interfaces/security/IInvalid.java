package com.haylion.haylionnetwork.interfaces.security;

import android.content.Context;


/**
 * Author:wangjianming
 * Time:2018/11/15 14:24
 * Description:IInvalid
 */
public interface IInvalid {
    /**
     * 强制重新登录
     *
     * @param context
     * @param msg
     */
    void reLogin(Context context, String msg);

    /**
     * 设置网络
     *
     * @param isConnect 是否连接
     */

    void showNetWorkState(boolean isConnect);
}
