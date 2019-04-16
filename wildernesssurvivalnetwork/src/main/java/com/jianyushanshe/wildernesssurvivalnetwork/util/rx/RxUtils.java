package com.jianyushanshe.wildernesssurvivalnetwork.util.rx;

import com.jianyushanshe.wildernesssurvivalnetwork.interfaces.view.ILoading;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
     * @param isLoading 是否显示loading
     * @param loading   显示loading的view
     * @param <T>
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
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                if (loading != null) {
                                    loading.showLoading();
                                }
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
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
