package me.shouheng.notepal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.zlw.main.recorderlib.RecordManager;

import java.io.File;
import java.sql.Timestamp;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.data.entity.WorkToDo;
import me.shouheng.notepal.databinding.ActivityCameraBinding;
import me.shouheng.notepal.expand.bean.WorkList;

import static me.shouheng.commons.event.UMEvent.PAGE_CAMERA;

/**
 * 相机界面
 *
 * @author dongyang_wu
 * @date 2019/7/25 10:44
 */
@PageName(name = PAGE_CAMERA)
public class CameraActivity extends CommonActivity<ActivityCameraBinding> {
    private JCameraView jCameraView;
    private StringBuffer imagePaths = new StringBuffer();
    private StringBuffer soundPaths = new StringBuffer();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @SuppressLint("ShowToast")
    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        jCameraView = getBinding().jcameraview;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        jCameraView.setTip("JCameraView Tip");
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.i("CJT", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                String path = FileUtil.saveBitmap("JCamera", bitmap);
                //  /storage/emulated/0/JCamera/picture_1564236920965.jpg
                // 传图片到服务器处理
                System.out.println(path);
                imagePaths.append(path + " ");
                jCameraView.onResume();
//                Intent intent = new Intent();
//                intent.putExtra("path", path);
//                setResult(101, intent);
//                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                String path = FileUtil.saveBitmap("JCamera", firstFrame);
                Log.i("CJT", "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
                finish();
            }
        });
        jCameraView.setLeftClickListener(() -> {
            WorkToDo build = WorkToDo.builder().createTime(new Timestamp(System.currentTimeMillis())).photoFilePaths(imagePaths.toString()).soundFilePaths("录音路径").build();
            Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
            //用Bundle携带数据
            Bundle bundle = new Bundle();
            bundle.putString("name", build.getCreateTime().toString());
            bundle.putString("itemData", build.getPhotoFilePaths());
            bundle.putString("itemType", String.valueOf(WorkList.ListBean.TYPE_PHOTO));
            intent.putExtras(bundle);
            startActivity(intent);
        });
//        recordManager.changeRecordDir("");
        // /storage/emulated/0/Record/record_20190727_22_15_53.wav
        RecordManager.getInstance().setRecordResultListener(result -> {
            soundPaths.append(result + " ");
            Toast.makeText(getContext(), "录音数据保存在---" + result, Toast.LENGTH_LONG).show();
        });
        addSubscription(RxMessage.class, RxMessage.CODE_PHOTO_DATA_CHANGE, rxMessage -> System.out.println(rxMessage.getObject()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content(R.string.text_save_or_discard)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_give_up)
                .onPositive((materialDialog, dialogAction) -> {
                    WorkToDo.builder().createTime(new Timestamp(System.currentTimeMillis())).photoFilePaths(imagePaths.toString()).soundFilePaths(soundPaths.toString()).build()
                            .saveAsync().listen(success -> ToastUtils.makeToast(success ? "数据保存成功" : "数据保存失败"));
                    finish();
                })
                .onNegative((materialDialog, dialogAction) -> {
                    finish();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jCameraView.onDestroy();
    }
}
