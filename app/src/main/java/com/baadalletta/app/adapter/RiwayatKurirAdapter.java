package com.baadalletta.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatKurirAdapter extends RecyclerView.Adapter<RiwayatKurirAdapter.MyHolderView> {

    private Customer customer;
    private Context context;
    private ArrayList<Pesanan> pesanans;

    public RiwayatKurirAdapter(Context context, ArrayList<Pesanan> pesanans) {
        this.context = context;
        this.pesanans = pesanans;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Pesanan> pesanans2) {
        pesanans = pesanans2;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RiwayatKurirAdapter.MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_kurir, parent, false);
        RiwayatKurirAdapter.MyHolderView myHolderView = new RiwayatKurirAdapter.MyHolderView(view);
        return myHolderView;
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatKurirAdapter.MyHolderView holder, int position) {

        String id_customer = String.valueOf(pesanans.get(position).getId_customer());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(id_customer);
        responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
            @Override
            public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {

                        customer = response.body().getData();

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

        holder.tv_update.setText("Update : " + pesanans.get(position).getUpdated_at().substring(0, 19));
        String status_pesana = pesanans.get(position).getStatus_pesanan();
        if (status_pesana.equals("done")) {
            holder.tv_status.setText("Selesai");
            holder.rl_lihat.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_primary_radius));
        } else {
            holder.tv_status.setText(status_pesana);
            holder.rl_lihat.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_mulai_disable_btn));
        }


    }

    @Override
    public int getItemCount() {
        return pesanans.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder {

        TextView tv_alamat;
        TextView tv_nama;
        TextView tv_kode;
        TextView tv_update;
        TextView tv_status;
        RelativeLayout rl_lihat;

        public MyHolderView(@NonNull View itemView) {
            super(itemView);

            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_kode = itemView.findViewById(R.id.tv_kode);
            tv_update = itemView.findViewById(R.id.tv_update);
            tv_status = itemView.findViewById(R.id.tv_status);
            rl_lihat = itemView.findViewById(R.id.rl_lihat);

        }
    }
}
