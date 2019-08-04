package me.shouheng.notepal.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.notepal.PalmApp;

public class ToastUtil {

    private static Toast sLastToast = null;

    public static void clear() {
        if (sLastToast != null) {
            sLastToast.cancel();
        }
    }

    @SuppressLint("CheckResult")
    private static void show(CharSequence text, int duration) {
        if (sLastToast != null) {
            sLastToast.cancel();
        }
        //注意，这里不可使用参数context作为makeText的输入，否则会导致sLastToast持有context，造成context泄露
        Flowable.just(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    Toast toast = Toast.makeText(PalmApp.getContext(), text, duration);
                    sLastToast = toast;
                    toast.show();
                });
    }

    public static void showNetworkOrNuableError(boolean isNetWorkError) {
        if (sLastToast != null) {
            sLastToast.cancel();
        }

        Observable.just(isNetWorkError ? "网络连接失败" : "发生内部错误").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<CharSequence>() {
                    @Override
                    public void onNext(CharSequence charSequence) {
                        Toast toast = Toast.makeText(PalmApp.getContext(), charSequence, Toast.LENGTH_SHORT);
                        sLastToast = toast;
                        toast.show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void show(Activity context, int resId) {
        if (null == context) {
            return;
        }
        show(context.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void showLong(Activity context, int resId) {
        if (null == context) {
            return;
        }
        show(context.getString(resId), Toast.LENGTH_LONG);
    }

}