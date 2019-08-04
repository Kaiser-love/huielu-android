package me.shouheng.notepal.data.network.service;

import android.support.annotation.NonNull;

import java.util.Map;

import io.reactivex.Observable;
import me.shouheng.notepal.data.network.config.URLConfig;
import me.shouheng.notepal.data.network.entity.ApiResult;
import me.shouheng.notepal.data.network.entity.FileUploadResult;
import me.shouheng.notepal.data.network.entity.UserEntity;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * @author dongyang_wu
 * @date 2019/7/26 19:23
 */
public interface Service {
    @POST(URLConfig.AUTH.URL)
    Observable<ApiResult<UserEntity>> auth(@Query(URLConfig.AUTH.ACCOUNT) String account,
                                           @Query(URLConfig.AUTH.PASSWORD) @NonNull String password);

    @Multipart
    @POST(URLConfig.UPLOADSINGLEFILE.URL)
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);

    @Multipart
    @POST(URLConfig.UPLOADFILES.URL)
    Observable<ApiResult<FileUploadResult>> uploadFiles(@Part(URLConfig.UPLOADFILES.NAME) String name, @Part(URLConfig.UPLOADFILES.MODE) Integer mode, @PartMap Map<String, RequestBody> files);

    @GET(URLConfig.DOWNLOADFILE.URL)
    @Streaming
    Call<ResponseBody> downloadFile(@Query(URLConfig.DOWNLOADFILE.ID) String id, @Query(URLConfig.DOWNLOADFILE.NAME) String name, @Query(URLConfig.DOWNLOADFILE.MODE) Integer mode);
}
