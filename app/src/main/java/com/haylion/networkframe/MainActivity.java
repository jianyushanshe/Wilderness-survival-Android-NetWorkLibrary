package com.haylion.networkframe;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haylion.haylionnetwork.base.BaseSubscriber;
import com.haylion.haylionnetwork.base.RxManage;
import com.haylion.haylionnetwork.bean.ResBase;
import com.haylion.haylionnetwork.http.broadcastreceiver.NetWorkBroadcastReceiver;
import com.haylion.haylionnetwork.http.exception.CommonException;
import com.haylion.haylionnetwork.http.util.NetWorkUtil;
import com.haylion.haylionnetwork.interfaces.security.IInvalid;
import com.haylion.haylionnetwork.interfaces.view.ILoading;
import com.haylion.haylionnetwork.util.rx.RxUtils;


import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements IInvalid, ILoading {

    private Button btGetInfo;
    private TextView tvInfo;
    private RxManage rxManage;
    private NetWorkBroadcastReceiver netWorkBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (netWorkBroadcastReceiver == null) {
            netWorkBroadcastReceiver = new NetWorkBroadcastReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkBroadcastReceiver, intentFilter);
        if (rxManage == null) {
            rxManage = new RxManage();
        }
        findViewById(R.id.bt_getinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
        findViewById(R.id.bt_getUserinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo();
            }
        });
        tvInfo = findViewById(R.id.tv_info);

    }


    private void getInfo() {
        BaseSubscriber<ResBase> baseSubscriber = new BaseSubscriber<ResBase>(this, this) {
            @Override
            protected void onUserSuccess(ResBase resBase) {
                //访问成功
                tvInfo.setText(resBase.toString());
            }

            @Override
            protected void onUserError(CommonException ex) {
                super.onUserError(ex);
                //用户需要处理的异常
                tvInfo.setText(ex.getMsg());
            }

            @Override
            protected void onUnifiedError(CommonException ex) {
                super.onUnifiedError(ex);
                //系统级别异常
                tvInfo.setText(ex.getMsg());
            }
        };

        Disposable disposable = MainNetWork.getInstance().getInfo(114.058531, 22.509481, 114.05853099999997, 22.509480998957397, 0)
                .compose(RxUtils.<ResBase>getScheduler(true, this))
                .subscribeWith(baseSubscriber);
        rxManage.add(disposable);
    }


    private void getUserInfo() {
        BaseSubscriber<ResBase> baseSubscriber = new BaseSubscriber<ResBase>(this, this) {
            @Override
            protected void onUserSuccess(ResBase resBase) {
                tvInfo.setText(resBase.toString());
            }

            @Override
            protected void onUserError(CommonException ex) {
                super.onUserError(ex);
                tvInfo.setText(ex.getMsg());
            }

            @Override
            protected void onUnifiedError(CommonException ex) {
                super.onUnifiedError(ex);
                tvInfo.setText(ex.getMsg());
            }
        };
        Disposable disposable = MainNetWork.getInstance().getUserInfo()
                .compose(RxUtils.<ResBase>getScheduler(true, this))
                .subscribeWith(baseSubscriber);
        rxManage.add(disposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxManage.clear();//清空所有订阅
        unregisterReceiver(netWorkBroadcastReceiver);
    }

    @Override
    public void reLogin(Context context, String msg) {
        //重新登录
        tvInfo.setText("Token过期请重新登录");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetWorkUtil.isNetConnected(getApplicationContext())) {
            tvInfo.setText("网络是否连接---是");
        } else {
            tvInfo.setText("网络是否连接---否");
        }

    }

    @Override
    public void showNetWorkState(boolean isConnect) {
        tvInfo.setText("网络状态变化：网络是否连接---" + isConnect);
    }

    @Override
    public void showLoading() {
        //显示数据加载中
        tvInfo.setText("数据加载中。。。");
    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }
}
