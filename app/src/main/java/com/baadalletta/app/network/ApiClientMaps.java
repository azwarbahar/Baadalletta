package com.baadalletta.app.network;

import com.baadalletta.app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientMaps {

    private static Retrofit retrofit;

    public static Retrofit getClient(){
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
