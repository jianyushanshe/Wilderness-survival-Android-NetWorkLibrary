package com.haylion.haylionnetwork.http.api;

import android.app.Application;
import android.text.TextUtils;

import com.ecar.util.TagUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haylion.haylionnetwork.db.SettingPreferences;
import com.haylion.haylionnetwork.http.converter.ConverterFactory;
import com.haylion.haylionnetwork.http.util.HttpsUtils;
import com.haylion.haylionnetwork.util.major.Major;
import com.haylion.haylionnetwork.util.time.TimeCalibrationInterceptor;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Author:wangjianming
 * Time:2018/11/15 17:13
 * Description:ApiBox retrofit代理包装类
 */
public class ApiBox {
    private int CONNECT_TIME_OUT = 10 * 1000;//跟服务器连接超时时间
    private int READ_TIME_OUT = 10 * 1000;    // 数据读取超时时间
    private int WRITE_TIME_OUT = 10 * 1000;   //数据写入超时时间
    private static final String CACHE_NAME = "cache";   //缓存目录名称
    /**
     * Log 日志开关 发布版本设为false
     */
    private boolean DEBUG = false;
    private boolean VeriNgis = true;

    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public boolean isVeriNgis() {
        return VeriNgis;
    }

    public void setVeriNgis(boolean veriNgis) {
        VeriNgis = veriNgis;
    }

    /**
     * 单例 持有引用
     */
    private final Gson gson;
    private final OkHttpClient okHttpClient;

    public Application application;//应用上下文(需注入参数)
    private File cacheFile;//缓存路径
    private final InputStream[] inputStreams;

    private Map<String, Object> serviceMap;


    /**
     * 在访问时创建单例
     */
    private static class SingletonHolder {
        private static ApiBox INSTANCE;
    }

    /**
     * 获取单例
     */
    public static ApiBox getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 创建Api服务实例的方法。
     *
     * @param serviceClass 接口类
     * @param baseUrl      网络请求url
     * @param <T>
     * @return
     */
    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = "";
        }
        Object serviceObj = serviceMap.get(serviceClass.getName() + baseUrl);
        if (serviceObj != null) {
            return (T) serviceObj;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        T service = retrofit.create(serviceClass);
        serviceMap.put(serviceClass.getName() + baseUrl, service);
        return service;
    }

    /**
     * 构造方法
     *
     * @param builder
     */
    private ApiBox(Builder builder) {
        //1.设置应用上下文、debug参数
        DEBUG = builder.debug;
        VeriNgis = builder.veriNgis;
        this.application = builder.application;
        this.cacheFile = builder.cacheDir;
        this.inputStreams = builder.inputStreams;
        SettingPreferences sp = SettingPreferences.getDefault(application);
        sp.setReqkey(builder.reqKey);
        this.serviceMap = new HashMap<>();
        if (builder.connetTimeOut > 0) {
            this.CONNECT_TIME_OUT = builder.connetTimeOut;
        }
        if (builder.readTimeOut > 0) {
            this.READ_TIME_OUT = builder.readTimeOut;
        }

        if (builder.writeTimeOut > 0) {
            this.WRITE_TIME_OUT = builder.writeTimeOut;
        }
        //2.gson
        gson = getReponseGson();

        //3.okhttp
        okHttpClient = getClient();
    }

    /**
     * 同步时间的拦截器
     *
     * @return
     */
    private Interceptor getTimeIntercepter() {
        return new TimeCalibrationInterceptor();
    }

    /**
     * 设置打印 log
     * 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
     *
     * @return
     */
    private HttpLoggingInterceptor getLogInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return interceptor;
    }

    /**
     * 创建okhttp客户端
     *
     * @return
     */
    private OkHttpClient getClient() {
        HostnameVerifier hostnameVerifier = HttpsUtils.getHostnameVerifier();
        // 如果使用到HTTPS，我们需要创建SSLSocketFactory，并设置到client
        SSLSocketFactory sslSocketFactory = HttpsUtils.getSslFactory();
        if (inputStreams != null) {
            sslSocketFactory = HttpsUtils.setCertificates(inputStreams);
        }
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(getLogInterceptor())
                .addInterceptor(getTimeIntercepter())
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(hostnameVerifier);
        return okHttpClientBuilder.build();
    }

    /**
     * 创建配置Gson对象
     *
     * @return
     */
    private Gson getReponseGson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson;
    }


    public static final class Builder {
        private Application application;//应用上下文，需要注入参数
        private File cacheDir;//缓存路径
        private boolean debug;
        private String appId;
        private String reqKey;
        private int connetTimeOut;
        private int readTimeOut;
        private int writeTimeOut;
        private boolean veriNgis = true;
        private InputStream[] inputStreams;

        public Builder application(Application application) {
            this.application = application;
            this.cacheDir = new File(application.getCacheDir(), CACHE_NAME);
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder veriNgis(boolean veriNgis) {
            this.veriNgis = veriNgis;//设置绕过参数
            return this;
        }

        public Builder reqKey(String reqKey) {
            this.reqKey = reqKey;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder connetTimeOut(int connetTime) {
            this.connetTimeOut = connetTime;
            return this;
        }

        public Builder readTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder writeTimeOut(int writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public Builder inputStreams(InputStream[] inputStreams) {
            this.inputStreams = inputStreams;
            return this;
        }

        public ApiBox build() {
            if (SingletonHolder.INSTANCE == null) {
                ApiBox apiBox = new ApiBox(this);
                SingletonHolder.INSTANCE = apiBox;
            } else {
                SingletonHolder.INSTANCE.application = this.application;
                SingletonHolder.INSTANCE.DEBUG = this.debug;
                SingletonHolder.INSTANCE.VeriNgis = this.veriNgis;
                SettingPreferences sp = SettingPreferences.getDefault(application);
                sp.setReqkey(this.reqKey);
                TagUtil.IS_SHOW_LOG = this.debug;
            }
            SettingPreferences sp = SettingPreferences.getDefault(application);
            sp.setReqkey(this.reqKey);
            sp.setAppId(Major.eUtil.binstrToStr(TextUtils.isEmpty(appId) ? "" : appId));

            return SingletonHolder.INSTANCE;
        }

    }

}
