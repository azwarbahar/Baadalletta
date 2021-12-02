package com.baadalletta.app.network;

import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.models.maps.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {


    // PESANAN MAPS
    @POST("kurir/get-pesanan-status/kurir/{kurir_id}")
    Call<ResponsePesananKurir> getPesananMaps(@Path("kurir_id") String kurir_id,
                                              @Query("status_pesanan") String status_pesanan);

    // PESANAN
    @GET("kurir/get-pesanan/{kurir_id}")
    Call<ResponsePesananKurir> getPesananIdKurir(@Path("kurir_id") String kurir_id);

    // MAPS
    @GET("maps/api/directions/json")
    Call<Result> getDirection(@Query("mode") String mode,
                              @Query("transit_routing_preferance") String preferance,
                              @Query("origin") String origin,
                              @Query("destination") String destination,
                              @Query("key") String key);

}
