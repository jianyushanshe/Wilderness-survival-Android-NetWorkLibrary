package com.haylion.haylionnetwork.base;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.haylion.haylionnetwork.bean.ResBase;
import com.haylion.haylionnetwork.http.api.ApiBox;
import com.haylion.haylionnetwork.http.exception.CommonException;
import com.haylion.haylionnetwork.http.exception.InvalidException;
import com.haylion.haylionnetwork.http.exception.UserException;
import com.haylion.haylionnetwork.http.util.TagLibUtil;
import com.haylion.haylionnetwork.interfaces.security.IInvalid;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Author:wangjianming
 * Time:2018/11/15 15:45
 * Description:BaseSubscriber 对统一的异常进行处理，只返回用户需要处理的数据或异常
 */
public abstract class BaseSubscriber<T> extends DisposableSubscriber<T> {

    private Context context;
    private IInvalid iInvalid;

    public BaseSubscriber(Context context, IInvalid iInvalid) {
        this.context = context;
        this.iInvalid = iInvalid;
    }

    @Override
    public void onNext(T t) {
        if (t instanceof ResBase) {
            ResBase resBase = (ResBase) t;
            if (resBase.code != Integer.parseInt(ApiBox.FLAG_SUCCESS_CODE)) {//失败
                if (resBase.code == Integer.parseInt(ApiBox.FLAG_TOKEN_EXPIRED)) {
                    //重新登录
                    showDialog(context, resBase.msg);
                } else {
                    this.onUserError(new CommonException(new UserException(resBase.code + "", resBase.msg, resBase)));
                }
            } else {
                this.onUserSuccess(t);
            }
        } else {
            this.onOtherNext(t);
        }
    }


    @Override
    public void onError(Throwable e) {
        CommonException ex = null;
        try {
            if (e instanceof UserException) {//用户需要处理的异常
                UserException userException = (UserException) e;
                ex = new CommonException(userException);
                onUserError(ex);
            } else if (e instanceof InvalidException) {//权限异常处理
                InvalidException invalidException = (InvalidException) e;
                if (InvalidException.FLAG_ERROR_RESPONCE_CHECK.equals(invalidException.getCode())) {
                    //校验码错误
                    onCheckNgisFailed(context, invalidException);
                } else if (InvalidException.FLAG_ERROR_RELOGIN.equals(invalidException.getCode())) {
                    //重新登录
                    showDialog(context, invalidException.getMsg());
                }
            } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
                //解析错误
                ex = new CommonException(e, ApiBox.FLAG_PARSE_ERROR);
                onUnifiedError(ex);
            } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof SocketException || e instanceof UnknownHostException) {
                //网络错误
                if (e instanceof SocketTimeoutException) {
                    ex = new CommonException(e, ApiBox.FLAG_NET_TIME_OUT);
                } else {
                    ex = new CommonException(e, ApiBox.FLAG_NET_ERROR);
                }
                onUnifiedError(ex);
            } else if (e instanceof SecurityException) {
                //权限未许可
                ex = new CommonException(e, ApiBox.FLAG_PERMISSION_ERROR);
                onUnifiedError(ex);
            } else {
                //未知错误
                ex = new CommonException(e, ApiBox.FLAG_UNKNOWN);
                onUnifiedError(ex);
            }
            resetContext();
            if (ApiBox.getInstance().isDEBUG() && ex != null) {
                e.printStackTrace();
                String errorMsg = ex.getMessage();
                TagLibUtil.showLogError(BaseSubscriber.class.getSimpleName() + ":" + errorMsg);
            }
        } catch (Exception exception) {
            resetContext();
            if (ApiBox.getInstance().isDEBUG()) {
                if (e != null) {
                    e.printStackTrace();
                }
                exception.printStackTrace();
            }

        }
    }


    @Override
    public void onComplete() {
        resetContext();
    }

    /**
     * 统一的 默认处理
     * 默认自动处理网络、解析、未知异常；弹提示
     * 有需要子类可以重写，不走super.onError 就可以屏蔽掉弹提示
     *
     * @param ex
     */
    protected void onUnifiedError(CommonException ex) {
        if (!ex.isDoNothing()) {
            showMsg(context, ex.getMsg());
        }
    }

    private void showDialog(Context context, String msg) {
        if (iInvalid != null) {
            iInvalid.reLogin(context, msg);
        }
    }


    protected void onOtherNext(T t) {

    }

    /**
     * 数据请求成功，用户处理数据
     *
     * @param t
     */
    protected abstract void onUserSuccess(T t);

    /**
     * 上下文置空
     */
    private void resetContext() {
        context = null;
    }

    /**
     * 自定义处理：
     * 接口返回 state 失败、自定义处理返回失败
     * 需要处理时 子类重写
     */
    protected void onUserError(CommonException ex) {
        if (!ex.isDoNothing()) {
            showMsg(context, ex.getMsg());
        }
    }

    /**
     * 提示msg
     *
     * @param context
     * @param msg
     */
    protected void showMsg(Context context, String msg) {
        if (context != null) {
            String reMsg = TextUtils.isEmpty(msg) ? "" : msg;
            TagLibUtil.showToast(context, reMsg);
        } else {
            TagLibUtil.showLogDebug("Subscriber 上下文为空");
        }
    }

    /**
     * 校验码失败
     *
     * @param context
     * @param invalidException
     */
    protected void onCheckNgisFailed(Context context, InvalidException invalidException) {
        if (context != null || invalidException != null) {
            String msg = TextUtils.isEmpty(invalidException.getMsg()) ? "" : invalidException.getMsg();
            TagLibUtil.showToast(context, msg);
        } else {
            TagLibUtil.showLogDebug("Subscriber 上下文为空");
        }
    }
}
