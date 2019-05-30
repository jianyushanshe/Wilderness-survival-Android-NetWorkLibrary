package com.haylion.haylionnetwork.util.rx;

import android.annotation.SuppressLint;
import android.util.TimeUtils;

import com.haylion.haylionnetwork.http.api.ApiBox;
import com.haylion.haylionnetwork.interfaces.view.ILoading;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:wangjianming
 * Time:2018/11/15 14:19
 * Description:RxUtils
 */
public class RxUtils {


    /**
     * 获取线程调度器
     *
     * @param isLoading            是否显示loading
     * @param loading              显示loading的view
     * @return
     */
    public static <T> FlowableTransformer<T, T> getScheduler(boolean isLoading, final ILoading loading) {
        FlowableTransformer<T, T> transformer = null;
        if (isLoading) {
            transformer = rxSchedulerLoading(loading);
        } else {
            transformer = rxScheduler();
        }
        return transformer;
    }

    /**
     * 订阅在子线程、观察和取消订阅在UI线程
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> rxScheduler() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onBackpressureDrop();
            }
        };
    }

    /**
     * 是否正在加载
     */
    private static boolean isLoading;

    /**
     * 1.订阅在子线程、观察和取消订阅在UI线程.
     * 2.事件序列触发 显示loading 、结束（onError、onCompleted）取消Loading
     *
     * @param loading
     * @param <T>
     * @return
     */

    private static <T> FlowableTransformer<T, T> rxSchedulerLoading(final ILoading loading) {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                isLoading = true;
                                Observable.timer(ApiBox.DELAY_TIME_SHOW_LOADING, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        if (loading != null && isLoading) {
                                            loading.showLoading();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                isLoading = false;
                                if (loading != null) {
                                    loading.dismissLoading();
                                }
                            }
                        })
                        .unsubscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onBackpressureDrop();

            }
        };
    }
}
