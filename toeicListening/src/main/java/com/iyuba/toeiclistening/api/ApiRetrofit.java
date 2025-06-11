package com.iyuba.toeiclistening.api;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iyuba on 2017/8/21.
 */

public class ApiRetrofit {

    private static ApiRetrofit apiRetrofit;
    private final Retrofit mRetrofit;
    private final Retrofit mRetrofit2;
    private final Retrofit mRetrofit3;
    private OkHttpClient mClient;
    private ApiService service;
    private YzPhoneNumber yzPhoneNumber;
    private ApiUpdateTestRecordInput apiUpdateTestRecordInput;
    private ApiUpdateWordsRecordInput apiUpdateWordsRecordInput;
    private static YzPhoneNumberForget yzPhoneNumberForget;

    private ApiRetrofit(){


        mClient = new OkHttpClient.Builder()
                .addInterceptor(mHeaderInterceptor)//添加头部信息拦截器
//                .addInterceptor(mLogInterceptor)//添加log拦截器
                .addInterceptor(getLogInterceptor())//添加log拦截器
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//支持RxJava
                .client(mClient)
                .build();

        mRetrofit2 = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_TESTRECORD_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持RxJava
                .client(mClient)
                .build();

        mRetrofit3 = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_TESTRECORD_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持RxJava
                .client(mClient)
                .build();

        service = mRetrofit.create(ApiService.class);
        yzPhoneNumber = mRetrofit.create(YzPhoneNumber.class);
        apiUpdateTestRecordInput = mRetrofit2.create(ApiUpdateTestRecordInput.class);
        apiUpdateWordsRecordInput = mRetrofit3.create(ApiUpdateWordsRecordInput.class);

    }

    private Interceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**请求访问quest和response拦截器*/
    private Interceptor mLogInterceptor =  new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            okhttp3.Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();

            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    };



    /**增加头部信息的拦截器*/
    private Interceptor mHeaderInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/8.0.0.13547");
            builder.addHeader("Cache-Control", "max-age=0");
            builder.addHeader("Upgrade-Insecure-Requests", "1");
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader("Cookie", "uuid=\"w:f2e0e469165542f8a3960f67cb354026\"; __tasessionId=4p6q77g6q1479458262778; csrftoken=7de2dd812d513441f85cf8272f015ce5; tt_webid=36385357187");
            return chain.proceed(builder.build());
        }
    };
    public YzPhoneNumber getYzPhoneNumber(){
       return yzPhoneNumber;
    }

    public YzPhoneNumberForget getYzPhoneNumberForget(){
        if(yzPhoneNumberForget == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(apiRetrofit.mClient)
                    .baseUrl(YzPhoneNumberForget.YZNUMBER_URL_FORGET)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            yzPhoneNumberForget = retrofit.create(YzPhoneNumberForget.class);
        }
        return yzPhoneNumberForget;
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();

                }
            }
        }
        return apiRetrofit;
    }

    public ApiService getApiService() {
        return service;
    }
    public ApiUpdateTestRecordInput getApiUpdateTestRecordInput(){
        return apiUpdateTestRecordInput;
    }
    public ApiUpdateWordsRecordInput getApiUpdateWordsRecordInput(){
        return apiUpdateWordsRecordInput;
    }
}
