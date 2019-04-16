package com.haylion.networkframe;

import com.haylion.haylionnetwork.bean.ResBase;

import java.util.TreeMap;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

/**
 * Author:wangjianming
 * Time:2019/4/16 16:01
 * Description:MainApi
 */
public interface MainApi {

    @GET("app/station/around")
    Flowable<ResBase> getInfo(@QueryMap TreeMap<String, Object> treeMap);

    @GET("app/wallet/info")
    Flowable<ResBase> getUserInfo(@Header("token") String token);

}
