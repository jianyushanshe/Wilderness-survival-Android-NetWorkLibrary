package com.jianyushanshe.wildernesssurvivalnetwork.interfaces.view;

import com.jianyushanshe.wildernesssurvivalnetwork.interfaces.security.IInvalid;

/**
 * Author:wangjianming
 * Time:2018/11/15 14:22
 * Description:ILoading
 */
public interface ILoading extends IInvalid {
    /**
     * 显示进度条
     */
    void showLoading();

    /**
     * 隐藏进度条
     */
    void dismissLoading();

    /**
     * 显示消息
     *
     * @param msg
     */
    void showMsg(String msg);
}
