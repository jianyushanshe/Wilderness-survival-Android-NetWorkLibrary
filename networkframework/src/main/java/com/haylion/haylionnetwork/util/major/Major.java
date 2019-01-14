package com.haylion.haylionnetwork.util.major;

import android.text.TextUtils;


import com.ecar.encryption.Epark.EparkEncrypUtil;
import com.ecar.factory.EncryptionUtilFactory;
import com.ecar.util.CastStringUtil;
import com.haylion.haylionnetwork.db.SettingPreferences;
import com.haylion.haylionnetwork.http.api.ApiBox;

import java.util.TreeMap;

/**
 * Author:wangjianming
 * Time:2018/11/15
 * Description:加密数据
 */
public class Major {

    public static EparkEncrypUtil eUtil = EncryptionUtilFactory.getDefault(true).createEpark();

    protected final static String PARAMS_CLIENT_TYPE = "ClientType";//

    protected final static String PARAMS_ANDROID = "android";//

    public final static String PARAMS_MODULE = "module";//

    public final static String PARAMS_SERVICE = "service";//

    public final static String PARAMS_METHOD = "method";//

    protected final static String comid = "200000002";

    public static final String PARAMS_USER_PHONE_NAME = "userPhoneNum";

    public static final String lastPath = "data";

    protected static SettingPreferences spUtil = SettingPreferences.getDefault(ApiBox.getInstance().application);

    /**
     * @throws Exception
     * @功能：获取带tkey的treemap
     * @param：
     * @return：
     */
    public static TreeMap<String, String> getTreeMapByT() {
        TreeMap<String, String> tMap = new TreeMap<String, String>();
        tMap.put(PARAMS_CLIENT_TYPE, PARAMS_ANDROID);
        if (spUtil.getU() != null) {
            tMap.put("u", spUtil.getU());
            tMap.put("t", spUtil.getT());
            tMap.put("ts", spUtil.getTs());
        }
        tMap.put(PARAMS_MODULE, "app");
        tMap.put(PARAMS_SERVICE, "Std");
        return tMap;
    }

    /**
     * @throws Exception
     * @功能：获取带vkey的treemap
     * @param：
     * @return：
     */
    public static TreeMap<String, String> getTreeMapByV() {
        TreeMap<String, String> tMap = new TreeMap<String, String>();
        tMap.put(PARAMS_CLIENT_TYPE, PARAMS_ANDROID);
        tMap.put("u", spUtil.getU());
        tMap.put("v", spUtil.getV());
        tMap.put("ts", spUtil.getTs());
        tMap.put(PARAMS_MODULE, "app");
        tMap.put(PARAMS_SERVICE, "Std");
        return tMap;
    }

    /**
     * @throws Exception
     * @功能：获取不带key的treemap
     * @param：
     * @return：
     */
    public static TreeMap<String, String> getTreeMapNoneKey() {
        TreeMap<String, String> tMap = new TreeMap<String, String>();
        tMap.put(PARAMS_MODULE, "app");
        tMap.put(PARAMS_SERVICE, "Std");
        tMap.put(PARAMS_CLIENT_TYPE, PARAMS_ANDROID);
        if (!TextUtils.isEmpty(spUtil.getTs())) {
            tMap.put("ts", spUtil.getTs());
        }
        return tMap;
    }

    /******************************end 获取三种keyMap end****************************************/

    /**
     * @throws Exception
     * @功能：加密方法（加encode）
     * @param：
     * @return：
     */
    public static TreeMap<String, String> securityKeyMethodEnc(TreeMap<String, String> tMap, boolean encode, boolean isSign, boolean isNeedVe) {
        return getSecurityMapKeys(tMap, encode, isSign, isNeedVe, SettingPreferences.getDefault(ApiBox.getInstance().application).getAppId(), SettingPreferences.getDefault(ApiBox.getInstance().application).getReqkey());

    }

    /**
     * @throws Exception
     * @功能：加密方法（加encode）
     * @param：
     * @return：
     */
    public static TreeMap<String, String> securityKeyMethodEnc(TreeMap<String, String> tMap) {
        return getSecurityMapKeys(tMap, true, true, true, SettingPreferences.getDefault(ApiBox.getInstance().application).getAppId(), SettingPreferences.getDefault(ApiBox.getInstance().application).getReqkey());

    }

    /**
     * @throws Exception
     * @功能：加密方法（不加encode）
     * @param：
     * @return：
     */
    public static TreeMap<String, String> securityKeyMethodNoEnc(TreeMap<String, String> tMap) {
        return getSecurityMapKeys(tMap, false, true, true, SettingPreferences.getDefault(ApiBox.getInstance().application).getAppId(), SettingPreferences.getDefault(ApiBox.getInstance().application).getReqkey());

    }


    /**
     * @throws Exception
     * @功能：获取拼接后的请求字符串
     * @param：encode:是否添加encode
     */
    protected static TreeMap<String, String> getSecurityMapKeys(TreeMap tMap, boolean encode, boolean isSign, boolean isNeedVe, String appid, String requestKey) {
        return CastStringUtil.stringToTreeMap(eUtil.getSecurityMapKeys(tMap, encode, isSign, isNeedVe, appid, eUtil.binstrToStr(requestKey)));
    }

}
