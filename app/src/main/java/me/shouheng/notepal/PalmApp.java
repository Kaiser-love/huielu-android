package me.shouheng.notepal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.kongzue.dialog.v2.DialogSettings;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection;
import com.previewlibrary.ZoomMediaLoader;
import com.umeng.commonsdk.UMConfigure;
import com.zlw.main.recorderlib.RecordManager;

import org.litepal.LitePal;

import java.util.concurrent.TimeUnit;

import me.shouheng.commons.BaseApplication;
import me.shouheng.data.BuildConfig;
import me.shouheng.notepal.data.network.RetrofitFactory;
import me.shouheng.notepal.fragment.album.utils.ImageManager.ImageLoaderManager;
import me.shouheng.notepal.image.ImageLoader;
import okhttp3.OkHttpClient;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_MATERIAL;
import static com.kongzue.dialog.v2.DialogSettings.THEME_DARK;
import static com.kongzue.dialog.v2.DialogSettings.THEME_LIGHT;
import static me.shouheng.notepal.data.network.RetrofitFactory.API;

/**
 * 重点：
 * 1.自动刷新到新的笔记历史栈里面，防止数据丢失；
 * 2.笔记编辑界面底部的按钮可以自定义，现在的按钮位置需要调整；
 * 3.打开笔记的时候先从OneDrive上面检查备份信息；
 * 4.备份的文件的名称需要改；
 * <p>
 * Created by WngShhng with passion and love on 2017/2/26..
 * Contact me : shouheng2015@gmail.com.
 */
public class PalmApp extends BaseApplication {
//    private ApplicationComponent mApplicationComponent;

    private static PalmApp mInstance;

    private static boolean passwordChecked;
    public static boolean isDebug;

    public static synchronized PalmApp getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        isDebug = BuildConfig.DEBUG;
        if (isDebug) {
            Stetho.initializeWithDefaults(this);
            UMConfigure.setLogEnabled(true);
        }
        initLitePal();
        initRecordManager();
        initImageLoader();
        initDialog();
        initOkDownload();
        ZoomMediaLoader.getInstance().init(new ImageLoader());
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
    }


    private void initLitePal() {
        LitePal.initialize(this);
    }

    private void initRecordManager() {
        RecordManager.getInstance().init(this, false);
    }

    private void initOkDownload() {
        OkHttpClient.Builder builder = RetrofitFactory.getInstance().getOkHttpClient().newBuilder();
        builder.connectTimeout(20_000, TimeUnit.SECONDS);
        builder.interceptors().clear();
        OkDownload.Builder downBuilder = new OkDownload.Builder(this);
        downBuilder.connectionFactory(new DownloadOkHttp3Connection.Factory().setBuilder(builder));
        OkDownload.setSingletonInstance(downBuilder.build());
    }

    private void initDialog() {
        DialogSettings.use_blur = true;                 //设置是否启用模糊
        DialogSettings.style = STYLE_MATERIAL;
        DialogSettings.tip_theme = THEME_DARK;
        DialogSettings.dialog_theme = THEME_LIGHT;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static boolean passwordNotChecked() {
        return !passwordChecked;
    }

    public static void setPasswordChecked() {
        PalmApp.passwordChecked = true;
    }

//    private void initComponent() {
//        mApplicationComponent = DaggerApplicationComponent.builder()
//                .applicationModule(new ApplicationModule(this))
//                .build();
//    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderManager.init(getApplicationContext());
    }
//    public ApplicationComponent getApplicationComponent() {
//        return mApplicationComponent;
//    }

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        view.setBackground(drawable);
    }
}
