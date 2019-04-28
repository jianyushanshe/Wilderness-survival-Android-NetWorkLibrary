package com.haylion.haylionnetwork.http.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.haylion.haylionnetwork.http.util.NetWorkUtil;
import com.haylion.haylionnetwork.interfaces.security.IInvalid;

/**
 * Author:wangjianming
 * Time:2019/4/26 10:12
 * Description:NetWorkBroadcastReceiver
 * 监听网络状态
 */
public class NetWorkBroadcastReceiver extends BroadcastReceiver {
    private IInvalid iInvalid;

    public NetWorkBroadcastReceiver(IInvalid iInvalid) {
        this.iInvalid = iInvalid;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (!NetWorkUtil.isNetConnected(context)) {
                //网络未连接
                if (iInvalid != null) {
                    iInvalid.showNetWorkState(false);
                }
            } else {
                //网络连接
                if (iInvalid != null) {
                    iInvalid.showNetWorkState(true);
                }
            }

        }
    }

}
