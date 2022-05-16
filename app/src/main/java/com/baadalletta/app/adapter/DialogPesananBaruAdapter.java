package com.baadalletta.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogPesananBaruAdapter extends RecyclerView.Adapter<DialogPesananBaruAdapter.MyHolderView> {

    private Customer customer;

    private Context context;
    private ArrayList<Pesanan> pesananArrayList;

    public DialogPesananBaruAdapter(Context context, ArrayList<Pesanan> pesananArrayList) {
        this.context = context;
        this.pesananArrayList = pesananArrayList;
    }

    @NonNull
    @Override
    public DialogPesananBaruAdapter.MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_dialog_list_pesanan, parent, false);
        DialogPesananBaruAdapter.MyHolderView myHolderView = new DialogPesananBaruAdapter.MyHolderView(view);
        return myHolderView;
    }

    @Override
    public void onBindViewHolder(@NonNull DialogPesananBaruAdapter.MyHolderView holder, int position) {

        String customer_id = String.valueOf(pesananArrayList.get(position).getId_customer());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
        responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
            @Override
            public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {

                        customer = response.body().getData();
                        holder.tv_nama.setText("Nama : " + customer.getNama());
                        holder.tv_kode.setText(customer.getKode());


                    } else {
                        Toast.makeText(context, "Data Customer tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsCustomer> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pesananArrayList.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder {

        private TextView tv_kode;
        private TextView tv_nama;

        public MyHolderView(@NonNull View itemView) {
            super(itemView);

            tv_kode = itemView.findViewById(R.id.tv_kode);
            tv_nama = itemView.findViewById(R.id.tv_nama);

        }
    }
}
