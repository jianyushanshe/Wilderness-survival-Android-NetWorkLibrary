package com.haylion.haylionnetwork.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:wangjianming
 * Time:2018/11/15 15:24
 * Description:ResBase
 */
public class ResBase<T> implements Serializable {
    /**
     * 接口所有msg 改成message  系统级错误 0x02 0x04 重新登录
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

//    /**
//     * json字符串
//     */
//    @SerializedName("data")
//    public String jsonStr;

    public T data;

    @Override
    public String toString() {
        return "ResBase{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", totalCount=" + totalCount +
                ", ts=" + ts +
                ", data='" + data + '\'' +
                '}';
    }
}
