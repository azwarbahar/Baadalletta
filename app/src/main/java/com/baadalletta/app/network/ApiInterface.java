package com.baadalletta.app.network;

import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.ResponsPesanan;
import com.baadalletta.app.models.ResponseKurir;
import com.baadalletta.app.models.ResponseLogin;
import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.models.ResponsePhoto;
import com.baadalletta.app.models.maps.Result;
import com.baadalletta.app.models.maps.distance.ResponseDistanceMaps;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    // LOGIN
    @POST("kurir/auth")
    Call<ResponseLogin> postLogin(@Query("username") String username,
                                  @Query("password") String password);

    // KURUR
    @GET("kurir/show/{kurir_id}")
    Call<ResponseKurir> getKurirId(@Path("kurir_id") String kurir_id);

    // UPDATE STATUS ONLINE
    @POST("kurir/update/status-akun/{kurir_id}")
    Call<ResponseKurir> setStatusAksi(@Path("kurir_id") String kurir_id,
                                      @Query("status_aksi") String status_aksi);

    // UPDATE STATUS PESNANA
    @POST("kurir/update/{pesanan_id}")
    Call<ResponsePhoto> updateStatusPesanan(@Path("pesanan_id") String pesanan_id,
                                            @Query("status_pesanan") String status_pesanan);


    // MAPS
    @GET("maps/api/directions/json")
    Call<Result> getDirection(@Query("mode") String mode,
                              @Query("transit_routing_preferance") String preferance,
                              @Query("origin") String origin,
                              @Query("destination") String destination,
                              @Query("key") String key);

    @GET("maps/api/distancematrix/json")
    Call<ResponseDistanceMaps> getDirectionMatrix(@Query("origins") String origins,
                                                  @Query("destinations") String destinations,
                                                  @Query("key") String key);


    // GET CUSTOMER
    @GET("kurir/get-customer/byId/{customer_id}")
    Call<ResponsCustomer> getCustomerId(@Path("customer_id") String customer_id);

    // UPDATE FOTO PENGANTARAN DAN FOTO PROFILE
    @Multipart
    @POST("kurir/update/file-foto/{pesanan_id}")
    Call<ResponsePhoto> updatePhoto(@Path("pesanan_id") String pesanan_id,
                                    @Part MultipartBody.Part foto,
                                    @Part("type") RequestBody type);


}
