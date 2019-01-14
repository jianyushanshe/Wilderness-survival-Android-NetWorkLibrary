package com.haylion.haylionnetwork.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Author:wangjianming
 * Time:2018/11/15 15:24
 * Description:ResBase
 */
public class ResBase<T> extends LitePalSupport implements Serializable {
    /**
     * 返回的结果code 200成功
     */
    public int code;

    /**
     * 列表总数
     */
    public int totalCount;
    /**
     * 时间戳
     */
    @SerializedName("ts")
    public long ts;

    public String message;
    public T data;

    public ResBase() {
    }


    @Override
    public String toString() {
        return "ResBase{" +
                "msg='" + message + '\'' +
                ", code=" + code +
                ", totalCount=" + totalCount +
                ", ts=" + ts +
                '}';
    }
}
