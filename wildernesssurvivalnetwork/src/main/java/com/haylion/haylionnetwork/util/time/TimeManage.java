package com.haylion.haylionnetwork.util.time;

import android.os.SystemClock;

/**
 * Author:wangjianming
 * Time:2018/11/15 15:07
 * Description:TimeManage
 * <p>
 * SystemClock.elapsedRealtime() ：手机系统开机时间（包含睡眠时间），用户无法在设置里面修改
 * 在必要的时刻获取一下服务器时间，然后记录这个时刻的手机开机时间（elapsedRealtime）
 * 后续时间获取：现在服务器时间 = 以前服务器时间 + 现在手机开机时间 - 以前服务器时间的获取时刻的手机开机时间
 * 使用：然后把软件调用System.currentTimeMillis()全部替换成TimeManager.getInstance().getServiceTime();
 */
public class TimeManage {
    private static TimeManage instance;
    /**
     * 以前服务器时间 - 以前服务器时间的获取时刻的系统启动时间
     */
    private long differenceTime;
    /**
     * 是否是服务器时间
     */
    private boolean isServerTime;

    private TimeManage() {
    }

    public static TimeManage getInstance() {
        if (instance == null) {
            synchronized (TimeManage.class) {
                if (instance == null) {
                    instance = new TimeManage();
                }
            }
        }
        return instance;
    }

    /**
     * 事件校准
     *
     * @param lastServiceTime 当前服务器时间
     * @return
     */
    public synchronized long initServerTime(long lastServiceTime) {
        differenceTime = lastServiceTime - SystemClock.elapsedRealtime();
        isServerTime = true;
        return lastServiceTime;
    }

}
