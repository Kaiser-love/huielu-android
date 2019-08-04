package me.shouheng.notepal.data.network.service;

import android.accounts.NetworkErrorException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.shouheng.notepal.data.network.entity.ApiResult;
import me.shouheng.notepal.util.ToastUtil;

/**
 * @author yemao
 * @date 2017/4/9
 * @description 写自己的代码, 让别人说去吧!
 */

public class BaseObserver<E> implements Observer<ApiResult<E>> {

    public static final String TAG = BaseObserver.class.getSimpleName();

    public BaseObserver() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(ApiResult<E> tBaseEntity) {
        onRequestEnd();
        if (tBaseEntity.isSuccess()) {
            try {
                onSuccess(tBaseEntity.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                onCodeError(tBaseEntity.getCode(), tBaseEntity.getMsg());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Throwable e) {
//        这里可以打印错误信息
//        Logger.w(TAG, "onError: ", e);
        onRequestEnd();
        e.printStackTrace();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                onFailure(e, true);
            } else {
                onFailure(e, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
    }

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    protected void onSuccess(E t) throws Exception{

    }

    /**
     * 返回成功了,但是code错误
     *
     * @param code 错误返回值
     * @param msg  错误信息
     * @throws Exception
     */
    protected void onCodeError(int code, String msg) throws Exception {
        ToastUtil.show(msg);
    }

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
        ToastUtil.showNetworkOrNuableError(isNetWorkError);
    }

    protected void onRequestStart() {
    }

    protected void onRequestEnd() {

    }


}