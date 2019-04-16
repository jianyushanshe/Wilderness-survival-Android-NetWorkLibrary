package com.jianyushanshe.networkframe;

import com.jianyushanshe.wildernesssurvivalnetwork.bean.ResBase;
import com.jianyushanshe.wildernesssurvivalnetwork.http.api.ApiBox;

import java.util.TreeMap;

import io.reactivex.Flowable;

/**
 * Author:wangjianming
 * Time:2019/4/16 16:10
 * Description:MainNetWork
 */
public class MainNetWork {
    public static MainNetWork mainNetWork;
    private static MainApi mainApi;
    private static final String baseUrl = "https://www.wanandroid.com/";

    public static synchronized MainNetWork getInstance() {
        if (mainNetWork == null) {
            mainNetWork = new MainNetWork();
        }
        if (mainApi == null) {
            mainApi = ApiBox.getInstance().createService(MainApi.class, baseUrl);
        }
        return mainNetWork;
    }


    public Flowable<ResBase> getInfo(double lng, double lat, double destLng, double destLat, int type) {
        Flowable<ResBase> flowable = null;
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("lng", lng);
        treeMap.put("lat", lat);
        flowable = mainApi.getInfo(treeMap);
        return flowable;
    }

    public Flowable<ResBase> getUserInfo() {
        Flowable<ResBase> flowable = null;
        flowable = mainApi.getUserInfo("");
        return flowable;
    }


}
