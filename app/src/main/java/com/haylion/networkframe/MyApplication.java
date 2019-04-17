package com.haylion.networkframe;

import android.app.Application;

import com.haylion.haylionnetwork.BuildConfig;
import com.haylion.haylionnetwork.http.api.ApiBox;

/**
 * Author:wangjianming
 * Time:2019/4/16 15:53
 * Description:MyApplication
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化ApiBox
        ApiBox.Builder builder = new ApiBox.Builder();
        builder.application(this)
                .debug(BuildConfig.DEBUG)//是否是debug模式
                .connetTimeOut(30000)//连接超时时间
                .readTimeOut(30000)//读取超时时间
                .jsonParseExceptionCode(1003)//json解析异常标识
                .successCode(200)//访问成功标识
                .tokenExpiredCode(406)//token过期，重新登录标识
                .unknownExceptionCode(1000)//未知异常标识
                .build();
    }
}
