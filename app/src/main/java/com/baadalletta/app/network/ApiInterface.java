package com.baadalletta.app.network;

import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.ResponsPesanan;
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

    @POST("kurir/set-pesanan-status/kurir/{pesanan_id}")
    Call<ResponsPesanan> setPesananKurir(@Path("pesanan_id") String pesanan_id,
                                         @Query("status_pesanan") String status_pesanan,
                                         @Query("id_kurir") int id_kurir);

    // PESANAN
    @GET("kurir/get-pesanan/{kurir_id}")
    Call<ResponsePesananKurir> getPesananIdKurir(@Path("kurir_id") String kurir_id);

    //


    // MAPS
    @GET("maps/api/directions/json")
    Call<Result> getDirection(@Query("mode") String mode,
                              @Query("transit_routing_preferance") String preferance,
                              @Query("origin") String origin,
                              @Query("destination") String destination,
                              @Query("key") String key);


    // GET CUSTOMER
    @GET("kurir/get-customer/byId/{customer_id}")
    Call<ResponsCustomer> getCustomerId(@Path("customer_id") String customer_id);

}
