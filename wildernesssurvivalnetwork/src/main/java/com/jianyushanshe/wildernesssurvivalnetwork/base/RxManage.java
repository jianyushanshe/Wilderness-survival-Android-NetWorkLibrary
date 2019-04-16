package com.jianyushanshe.wildernesssurvivalnetwork.base;

import com.jianyushanshe.wildernesssurvivalnetwork.util.rx.RxUtils;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Author:wangjianming
 * Time:2018/11/15 11:10
 * Description:RxManage  负责管理订阅关系
 */
public class RxManage {
    /**
     * 管理订阅者，取消订阅后，不能再次订阅
     */
    private CompositeDisposable subscriptions;
    /**
     * 添加有标识的订阅者
     */
    private HashMap<String, Disposable> mDisposableHashMap;

    /**
     * RxManage构造方法
     */
    public RxManage() {
        this.subscriptions = new CompositeDisposable();
        this.mDisposableHashMap = new HashMap<>();
    }

    /**
     * 将订阅者添加到订阅管理，方便取消订阅
     *
     * @param disposable
     */
    public void add(Disposable disposable) {
        if (subscriptions == null) {
            subscriptions = new CompositeDisposable();
        }
        if (disposable != null && !subscriptions.isDisposed()) {
            subscriptions.add(disposable);
        }
    }

    /**
     * 将标识的订阅者添加到订阅管理
     *
     * @param key        订阅者标识
     * @param disposable 订阅者
     */
    public void add(String key, Disposable disposable) {
        if (disposable != null) {
            mDisposableHashMap.put(key, disposable);
            add(disposable);
        }
    }

    /**
     * 取消订阅
     */
    public void clear() {
        if (subscriptions != null) {
            subscriptions.dispose();
            subscriptions = null;
        }
    }

    /**
     * 取消指定的订阅者
     *
     * @param key 订阅者标识
     */
    public void clear(String key) {
        Disposable disposable = mDisposableHashMap.get(key);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            subscriptions.delete(disposable);
        }
    }

    /**
     * 被观察者、订阅者 作为参数传入
     * 将订阅者添至管理
     *
     * @param observable
     * @param subscriber
     */
    public void addSubscription(Flowable observable, ResourceSubscriber subscriber) {
        if (observable != null && subscriber != null) {
            subscriptions.add((Disposable) observable.compose(RxUtils.rxScheduler()).subscribeWith(subscriber));
        }
    }
}
