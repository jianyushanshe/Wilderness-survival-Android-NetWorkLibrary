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
     * 消息
     */
    @SerializedName(value = "message", alternate = "resultmsg")
    public String msg;
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

    public T data;

    public ResBase() {
    }


    @Override
    public String toString() {
        return "ResBase{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", totalCount=" + totalCount +
                ", ts=" + ts +
                ", data=" + data +
                '}';
    }
}
