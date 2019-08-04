package me.shouheng.notepal.data.network;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.charset.Charset;

import me.shouheng.notepal.data.network.entity.ApiResult;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;


/**
 * @author yemao
 * @date 2017/4/9
 * @description 拦截器工具类!
 */

public class InterceptorUtil {
    public static String TAG = "----";
    public final static Charset UTF8 = Charset.forName("UTF-8");

    //日志拦截器
    public static HttpLoggingInterceptor LogInterceptor() {
        return new HttpLoggingInterceptor(message ->
                Log.d(TAG, "network: " + message))
                .setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印数据的级别
    }

    /**
     * token验证的拦截器
     *
     * @return
     */
    public static Interceptor loginInterceptor() {
        return chain -> {
            Request mRequest = chain.request();
            Response mResponse = chain.proceed(mRequest);
            ApiResult mMessage = decodeResponse(mResponse, ApiResult.class);
            return mResponse;
        };
    }

    private static <T extends ApiResult<E>, E> T decodeResponse(Response response, Class<T> tClass) throws IOException {
        ResponseBody responseBody = response.body();
        //得到缓冲源
        BufferedSource source = null;
        if (responseBody != null) {
            source = responseBody.source();
        }
        //请求全部
        if (source != null) {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        }
        Buffer buffer = null;
        if (source != null) {
            buffer = source.buffer();
        }
        Charset charset = UTF8;
        MediaType contentType = null;
        if (responseBody != null) {
            contentType = responseBody.contentType();
        }

        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        try {

            if (buffer != null && charset != null) {
                String bodyString = buffer.clone().readString(charset);
                return JSON.parseObject(bodyString, tClass);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}