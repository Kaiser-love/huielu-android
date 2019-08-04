package me.shouheng.notepal.data.network;

import android.annotation.SuppressLint;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.schedulers.Schedulers;
import me.shouheng.notepal.data.network.config.CookieManager;
import me.shouheng.notepal.data.network.config.HttpConfig;
import me.shouheng.notepal.data.network.config.SSLSocketClient;
import me.shouheng.notepal.data.network.service.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.shouheng.notepal.PalmApp.isDebug;

public class RetrofitFactory {

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型


    private static final String KEY_STORE_PASSWORD = "****";//证书密码（应该是客户端证书密码）
    private static final String KEY_STORE_TRUST_PASSWORD = "***";//授信证书密码（应该是服务端证书密码）

    public static String apiToken = "eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpc3N1c2VyIiwiYXVkIjoiYXVkaWVuY2UiLCJ1c2VyX2lkIjoiMSIsInVzZXJfbmFtZSI6InN0cmluZyIsImFjY291bnQiOiJhZG1pbiIsInRlbmFudF9jb2RlIjoiMDAwMDAwIiwiY2xpZW50X2lkIjoic3dvcmQiLCJleHAiOjE1NjQyMjM1NjcsIm5iZiI6MTU2NDIxNzU2N30.bg5nBbrPaDlN7CbEwXrJ9qu8dQe4uuuLbxAWvSv_bjo";
    private static RetrofitFactory mRetrofitFactory;
    private static Service service;
    OkHttpClient mOkHttpClient;

    private RetrofitFactory() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
//                .addInterceptor(InterceptorUtil.tokenInterceptor())
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .cookieJar(new CookieManager())
                .addInterceptor(InterceptorUtil.LogInterceptor())//添加日志拦截器
//                .addInterceptor(InterceptorUtil.loginInterceptor())
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("HUIELU-AUTH", "Basic c3dvcmQ6c3dvcmRfc2VjcmV0")
                            .addHeader("HUIELU-TOKEN", apiToken)
                            .addHeader("Accept-Encoding", "identity")
                            .build();
                    Response response = chain.proceed(request);
                    String token = response.header("HUIELU-TOKEN");
                    System.out.println(token);
                    if (!StringUtils.isEmpty(token)) {
                        apiToken = token;
                    }
                    return response;
                });
//                .addNetworkInterceptor(chain -> null)
        mOkHttpClient = builder.build();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(HttpConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava转换器
                .client(mOkHttpClient)
                .build();

        service = mRetrofit.create(Service.class);
        API().auth("admin", "admin")
                //延时两秒，第一个参数是数值，第二个参数是事件单位
                .delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(System.out::println, System.out::println);
    }

    public static RetrofitFactory getInstance() {
        if (mRetrofitFactory == null) {
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null)
                    mRetrofitFactory = new RetrofitFactory();
            }

        }
        return mRetrofitFactory;
    }

    static {
        synchronized (RetrofitFactory.class) {
            if (mRetrofitFactory == null)
                mRetrofitFactory = new RetrofitFactory();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static SSLSocketFactory getSocketFactory() {


//        InputStream trust_input = context.getResources().openRawResource(R.raw.trust);//服务器授信证书
        InputStream trust_input = null;//服务器授信证书
//        InputStream client_input = context.getResources().openRawResource(R.raw.client);//客户端证书
        InputStream client_input = null;//客户端证书
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            if (isDebug) {
                sslContext.init(null, new TrustManager[]{new TrustAllManager()},
                        new SecureRandom());
            } else {
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            }
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                trust_input.close();
                client_input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TrustAllManager implements X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

//    private static class TrustAllHostnameVerifier implements HostnameVerifier {
//        @Override
//        public boolean verify(String hostname, SSLSession session) {
//            return new X509Certificate[0];
//        }
//    }


    public static Service API() {
        return service;
    }
}