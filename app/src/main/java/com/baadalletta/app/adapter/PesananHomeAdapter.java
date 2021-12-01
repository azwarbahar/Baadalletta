package com.baadalletta.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.ui.DetailPesananActivity;

import java.util.ArrayList;

public class PesananHomeAdapter extends RecyclerView.Adapter<PesananHomeAdapter.MyHolderView> {

    private Context context;
    private ArrayList<Pesanan> pesananArrayList;
    private Customer customer;

    public PesananHomeAdapter(Context context, ArrayList<Pesanan> pesananArrayList) {
        this.context = context;
        this.pesananArrayList = pesananArrayList;
    }

    @NonNull
    @Override
    public PesananHomeAdapter.MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_pesanan_home, parent, false);
        PesananHomeAdapter.MyHolderView myHolderView = new PesananHomeAdapter.MyHolderView(view);
        return myHolderView;
    }

    @Override
    public void onBindViewHolder(@NonNull PesananHomeAdapter.MyHolderView holder, int position) {

        customer = pesananArrayList.get(position).getCustomer();

        holder.tv_nomor.setText(String.valueOf(position+1));
        holder.tv_alamat.setText(customer.getAlamat());
        holder.tv_nama.setText("Nama : " + customer.getNama());
        holder.tv_kode.setText("Kode : " + customer.getKode());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, DetailPesananActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return pesananArrayList.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder {

        private TextView tv_nomor;
        private TextView tv_alamat;
        private TextView tv_nama;
        private TextView tv_kode;
        private TextView tv_jarak;

        public MyHolderView(@NonNull View itemView) {
            super(itemView);

            tv_nomor = itemView.findViewById(R.id.tv_nomor);
            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_kode = itemView.findViewById(R.id.tv_kode);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);

        }
    }
}
