package com.haylion.haylionnetwork.http.util;

import com.haylion.haylionnetwork.db.SettingPreferences;
import com.haylion.haylionnetwork.http.api.ApiBox;
import com.haylion.haylionnetwork.util.major.Major;

/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:校验
 */
public class InvalidUtil {


    /**
     * 验证是否符合sign规则
     *
     * @param
     * @return true 符合sign规则
     */
    public static boolean checkSign(String sign, String content) {
        return Major.eUtil.checkSign(sign, content, SettingPreferences.getDefault(ApiBox.getInstance().application).getReqkey());
    }


}
