package com.baadalletta.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.ui.DetailPesananActivity;
import com.baadalletta.app.utils.Constanta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananHomeAdapter extends RecyclerView.Adapter<PesananHomeAdapter.MyHolderView> {

    private Customer customer;

    private Context context;
    private ArrayList<Pesanan> pesananArrayList;
    private PesananListRecyclerClickListener mClickListener;

    public PesananHomeAdapter(Context context, ArrayList<Pesanan> pesananArrayList, PesananListRecyclerClickListener mClickListener) {
        this.context = context;
        this.pesananArrayList = pesananArrayList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public PesananHomeAdapter.MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_pesanan_home, parent, false);
        PesananHomeAdapter.MyHolderView myHolderView = new PesananHomeAdapter.MyHolderView(view, mClickListener);
        return myHolderView;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull PesananHomeAdapter.MyHolderView holder, int position) {

        String customer_id = String.valueOf(pesananArrayList.get(position).getId_customer());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
        responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
            @Override
            public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {
                if (response.isSuccessful()){
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")){

                        customer = response.body().getData();
                        String lat = customer.getLatitude();
                        String lon = customer.getLongitude();
                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA));
                        loc1.setLongitude(Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA));
                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(lat));
                        loc2.setLongitude(Double.parseDouble(lon));
                        float distanceInMeters = loc1.distanceTo(loc2)/1000;
                        float distanceInKilometers = loc1.distanceTo(loc2)/1000;

                        String jarak_subs = String.valueOf(distanceInKilometers).substring(0,1);
                        if (jarak_subs.equals("0")){
                            String jarak_meter = String.valueOf(distanceInMeters).substring(0,5);
                            holder.tv_jarak.setText(jarak_meter+ " Meter");
                        } else {
                            String jarak_meter = String.valueOf(distanceInKilometers).substring(0,4);
                            holder.tv_jarak.setText(jarak_meter+ " Km");
                        }

                        holder.tv_nomor.setText(String.valueOf(position + 1));
                        holder.tv_alamat.setText(customer.getAlamat());
                        holder.tv_nama.setText("Nama : " + customer.getNama());
                        holder.tv_kode.setText("Kode : " + customer.getKode());


                    } else {
                        Toast.makeText(context, "Data Customer tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsCustomer> call, Throwable t) {

            }
        });

        holder.rl_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailPesananActivity.class);
                intent.putExtra("Extra_data", pesananArrayList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pesananArrayList.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_nomor;
        private TextView tv_alamat;
        private TextView tv_nama;
        private TextView tv_kode;
        private TextView tv_jarak;

        private RelativeLayout rl_lihat;

        PesananListRecyclerClickListener pesananListRecyclerClickListener;


        public MyHolderView(@NonNull View itemView, PesananListRecyclerClickListener clickListener) {
            super(itemView);

            rl_lihat = itemView.findViewById(R.id.rl_lihat);
            tv_nomor = itemView.findViewById(R.id.tv_nomor);
            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_kode = itemView.findViewById(R.id.tv_kode);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);

            pesananListRecyclerClickListener = clickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            pesananListRecyclerClickListener.onPesananClicked(getAdapterPosition());
        }
    }

    public interface PesananListRecyclerClickListener{
        void onPesananClicked(int position);
    }

}
