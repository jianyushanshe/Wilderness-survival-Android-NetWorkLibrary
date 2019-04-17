package com.haylion.haylionnetwork.http.util;

import android.content.Context;

import android.widget.Toast;

import com.haylion.haylionnetwork.http.api.ApiBox;
import com.orhanobut.logger.Logger;


public class TagLibUtil {
    private final static String TAG = "TagLibUtil";

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str,  Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示debug 数据
     *
     * @param str
     */
    public static void showLogDebug(String str) {
        if (ApiBox.getInstance().isDEBUG()) {
            Logger.d(TAG, str);
        }
    }

    /**
     * 显示debug 数据
     *
     * @param str
     */
    public static void showLogDebug(Class context, String str) {
        if (ApiBox.getInstance().isDEBUG()) {
            Logger.d(TAG, "<" + context.getName().toString() + ">--" + str);
        }
    }

    public static void showLogError(String str) {
        if (ApiBox.getInstance().isDEBUG()) {
            Logger.e(TAG, str);
        }
    }

    /**
     * 显示debug 数据
     *
     * @param str
     */
    public static void showLogError(Class context, String str) {
        if (ApiBox.getInstance().isDEBUG()) {
            Logger.e(TAG, "<" + context.getName().toString() + ">--" + str);
        }
    }

    public static void showLogDebug(String tag, String content) {
        if (ApiBox.getInstance().isDEBUG()) {
            Logger.d(tag, content);
        }
    }

}
