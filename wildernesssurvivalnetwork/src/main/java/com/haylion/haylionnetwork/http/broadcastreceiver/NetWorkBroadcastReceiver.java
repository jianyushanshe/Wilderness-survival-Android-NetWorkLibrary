package com.haylion.haylionnetwork.http.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.haylion.haylionnetwork.http.util.NetWorkUtil;
import com.haylion.rxbuspublic.rxbuslib.RxBus;

/**
 * Author:wangjianming
 * Time:2019/4/26 10:12
 * Description:NetWorkBroadcastReceiver
 * 监听网络状态
 */
public class NetWorkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (!NetWorkUtil.isNetConnected(context)) {
                //网络未连接
                RxBus.getInstance().post(false, Tags.EXTRA_NET_WORK_ISCONNECTED);
            } else {
                //网络连接
                RxBus.getInstance().post(true, Tags.EXTRA_NET_WORK_ISCONNECTED);
            }

        }
    }

    public interface Tags {
        String EXTRA_NET_WORK_ISCONNECTED = "extra_net_work_isconnected";
    }

}
