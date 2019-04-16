package com.jianyushanshe.wildernesssurvivalnetwork.http.api;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jianyushanshe.wildernesssurvivalnetwork.http.converter.ConverterFactory;
import com.jianyushanshe.wildernesssurvivalnetwork.http.util.HttpsUtils;
import com.jianyushanshe.wildernesssurvivalnetwork.util.time.TimeCalibrationInterceptor;

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
     * 异常
     */
    public static String FLAG_UNKNOWN = "1001";
    /**
     * 网络异常标志
     */
    public static String FLAG_NET_ERROR = "1002";
    /**
     * 网络异常标志
     */
    public static String FLAG_NET_TIME_OUT = "10021";
    /**
     * 解析异常
     */
    public static String FLAG_PARSE_ERROR = "1003";
    /**
     * 权限异常
     */
    public static String FLAG_PERMISSION_ERROR = "1004";
    /**
     * token过期，重新登录
     */
    public static String FLAG_TOKEN_EXPIRED = "406";
    /**
     * successCode
     */
    public static String FLAG_SUCCESS_CODE = "200";
    /**
     * Log 日志开关 发布版本设为false
     */
    private boolean DEBUG = false;

    public boolean isDEBUG() {
        return DEBUG;
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
        this.application = builder.application;
        this.cacheFile = builder.cacheDir;
        this.inputStreams = builder.inputStreams;
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
        if (builder.successCode > 0) {
            FLAG_SUCCESS_CODE = String.valueOf(builder.successCode);
        }
        if (builder.tokenExpiredCode > 0) {
            FLAG_TOKEN_EXPIRED = String.valueOf(builder.tokenExpiredCode);
        }
        if (builder.jsonParseExceptionCode > 0) {
            FLAG_PARSE_ERROR = String.valueOf(builder.jsonParseExceptionCode);
        }
        if (builder.netTimeOutExceptionCode > 0) {
            FLAG_NET_TIME_OUT = String.valueOf(builder.netTimeOutExceptionCode);
        }
        if (builder.permissionExceptionCode > 0) {
            FLAG_PERMISSION_ERROR = String.valueOf(builder.permissionExceptionCode);
        }
        if (builder.unknownExceptionCode > 0) {
            FLAG_UNKNOWN = String.valueOf(builder.unknownExceptionCode);
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
        private int connetTimeOut;
        private int readTimeOut;
        private int writeTimeOut;
        private InputStream[] inputStreams;
        /**
         * token过期代码，需要重新标识
         */
        private int tokenExpiredCode;
        /**
         * josn解析异常标识
         */
        private int jsonParseExceptionCode;
        /**
         * 网络连接超时异常标识
         */
        private int netTimeOutExceptionCode;
        /**
         * 权限异常标识
         */
        private int permissionExceptionCode;
        /**
         * 未标记异常标识
         */
        private int unknownExceptionCode;
        /**
         * 访问成功标识
         */
        private int successCode;

        public Builder application(Application application) {
            this.application = application;
            this.cacheDir = new File(application.getCacheDir(), CACHE_NAME);
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
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

        public Builder tokenExpiredCode(int tokenExpiredCode) {
            this.tokenExpiredCode = tokenExpiredCode;
            return this;
        }

        public Builder jsonParseExceptionCode(int jsonParseExceptionCode) {
            this.jsonParseExceptionCode = jsonParseExceptionCode;
            return this;
        }

        public Builder netTimeOutExceptionCode(int netTimeOutExceptionCode) {
            this.netTimeOutExceptionCode = netTimeOutExceptionCode;
            return this;
        }

        public Builder permissionExceptionCode(int permissionExceptionCode) {
            this.permissionExceptionCode = permissionExceptionCode;
            return this;
        }

        public Builder unknownExceptionCode(int unknownExceptionCode) {
            this.unknownExceptionCode = unknownExceptionCode;
            return this;
        }

        public Builder successCode(int successCode) {
            this.successCode = successCode;
            return this;
        }


        public ApiBox build() {
            if (SingletonHolder.INSTANCE == null) {
                ApiBox apiBox = new ApiBox(this);
                SingletonHolder.INSTANCE = apiBox;
            } else {
                SingletonHolder.INSTANCE.application = this.application;
                SingletonHolder.INSTANCE.DEBUG = this.debug;
            }
            return SingletonHolder.INSTANCE;
        }

    }

}
