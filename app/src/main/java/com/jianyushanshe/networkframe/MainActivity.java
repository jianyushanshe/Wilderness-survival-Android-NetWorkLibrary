package com.jianyushanshe.networkframe;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jianyushanshe.wildernesssurvivalnetwork.base.BaseSubscriber;
import com.jianyushanshe.wildernesssurvivalnetwork.base.RxManage;
import com.jianyushanshe.wildernesssurvivalnetwork.bean.ResBase;
import com.jianyushanshe.wildernesssurvivalnetwork.http.exception.CommonException;
import com.jianyushanshe.wildernesssurvivalnetwork.interfaces.security.IInvalid;
import com.jianyushanshe.wildernesssurvivalnetwork.interfaces.view.ILoading;
import com.jianyushanshe.wildernesssurvivalnetwork.util.rx.RxUtils;


import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements IInvalid, ILoading {

    private Button btGetInfo;
    private TextView tvInfo;
    private RxManage rxManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public void reLogin(Context context, String msg) {
        //重新登录
        tvInfo.setText("Token过期请重新登录");
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