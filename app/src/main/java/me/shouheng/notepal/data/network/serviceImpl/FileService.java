package me.shouheng.notepal.data.network.serviceImpl;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.liulishuo.okdownload.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.notepal.data.network.RetrofitFactory;
import me.shouheng.notepal.data.network.config.HttpConfig;
import me.shouheng.notepal.data.network.entity.FileUploadResult;
import me.shouheng.notepal.data.network.service.BaseObserver;
import me.shouheng.notepal.manager.FileManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static me.shouheng.notepal.data.network.RetrofitFactory.API;

/**
 * 文件上传
 *
 * @author dongyang_wu
 * @date 2019/7/26 20:13
 */
public class FileService {
    private Map<String, RequestBody> params;
    private volatile static FileService instance;

    public static FileService getInstance() {
        if (instance == null) {
            synchronized (FileService.class) {
                if (instance == null)
                    instance = new FileService();
            }
        }
        return instance;
    }

    @SuppressLint("CheckResult")
    public void uploadFilesToPpt(String filePaths, String name, Integer mode, BaseObserver<FileUploadResult> callback) {
        params = new HashMap<>();
        String[] paths = filePaths.split(" ");
        for (String path : paths) {
            File file = new File(path);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            params.put("file\"; filename=\"" + file.getName(), requestBody);
            addParameter(null, file);
        }
        // 1.
        //doOnSubscribe是事件被订阅之前(也就是事件源发起之前)会调用的方法，这个方法一般用于修改、添加或者删除事件源的数据流。
        //2.
        //doOnNext是观察者被通知之前(也就是回调之前)会调用的方法，说白了就是最终回调之前的前一个回调方法，
        // 这个方法一般做的事件类似于观察者做的事情，只是自己不是最终的回调者。（观察者即最终回调者）
        API().uploadFiles(name, mode, params)
                .subscribeOn(Schedulers.io())
                .doOnNext(fileUploadResultApiResult -> {
                    File filePath = Environment.getExternalStoragePublicDirectory("PDF");
                    FileManager.createFile(filePath);
                    String url = HttpConfig.DOWNLOAD_URL;
//                    DownLoadTaskByUrl(url, filePath, "测试.ppt").enqueue(new DownloadListener2() {
//                        @Override
//                        public void taskStart(@NonNull DownloadTask task) {
//                            System.out.println("开启");
//                        }
//
//                        @Override
//                        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
//                            System.out.println("结束" + cause);
//                            System.out.println(realCause);
//                        }
//                    });
                    API().downloadFile(fileUploadResultApiResult.getData().getObject().getFileId(), name, mode).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                writeFileToSDCard(name, mode == 0 ? "ppt" : "pdf", response.body());
                                System.out.println("下载成功");
                            } else {
                                System.out.println("失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);

    }

    public FileService addParameter(String key, Object o) {
        if (o instanceof String) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), (String) o);
            params.put(key, body);
        } else if (o instanceof File) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data;charset=utf-8"), (File) o);
            params.put("files\"; filename=\"" + ((File) o).getName() + "", body);
        }
        return this;
    }

    private boolean writeFileToSDCard(String name, String type, ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(type.toUpperCase()), name + "." + type.toLowerCase());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    System.out.println("下载：" + fileSizeDownloaded);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public DownloadTask DownLoadTaskByUrl(String url, File parentFile, String fileName) {
        Map<String, List<String>> header = new HashMap<>();
        header.put(HttpConfig.TOKEN_HEADER, Arrays.asList(RetrofitFactory.apiToken));
        return new DownloadTask.Builder(url, parentFile)
                .setHeaderMapFields(header)
                .setFilename(fileName)
                .setMinIntervalMillisCallbackProcess(16)
                .setConnectionCount(1)
                .setPassIfAlreadyCompleted(true)
                .build();
    }
}
