package com.baadalletta.app.network;

import com.baadalletta.app.models.ResponsePesananKurir;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    // PESANAN
    @GET("kurir/get-pesanan/{kurir_id}")
    Call<ResponsePesananKurir> getPesananIdKurir(@Path("kurir_id") String kurir_id);
}
