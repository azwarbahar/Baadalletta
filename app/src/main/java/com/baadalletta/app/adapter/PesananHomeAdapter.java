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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.BuildConfig;
import com.baadalletta.app.R;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.Place;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.maps.distance.Distance;
import com.baadalletta.app.models.maps.distance.Duration;
import com.baadalletta.app.models.maps.distance.ElementsItem;
import com.baadalletta.app.models.maps.distance.ResponseDistanceMaps;
import com.baadalletta.app.models.maps.distance.RowsItem;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiClientMaps;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.ui.DetailPesananActivity;
import com.baadalletta.app.ui.HomeActivity;
import com.baadalletta.app.utils.Constanta;

import java.util.ArrayList;
import java.util.List;

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

        String status_pesanan = pesananArrayList.get(position).getStatus_pesanan();
        String latling_distance = pesananArrayList.get(position).getTitik_koordinat();

        if (status_pesanan.equals("done")){
            holder.rl_continer_list.setBackground(ContextCompat.getDrawable(
                    context, R.color.ColorPrimaryGrey));
        } else {
            holder.rl_continer_list.setBackground(ContextCompat.getDrawable(
                    context, R.color.ColorPrimary102));
        }

        String lat_ba = Constanta.LATITUDE_BAADALLETTA;
        String longi_ba = Constanta.LONGITUDE_BAADALLETTA;
        String latling_origin = lat_ba + "," + longi_ba;

        ApiInterface apiInterface2 = ApiClientMaps.getClient().create(ApiInterface.class);
        Call<ResponseDistanceMaps> responseDistanceMapsCall = apiInterface2.getDirectionMatrix(latling_origin,
                latling_distance, BuildConfig.API_KEY_MAPS);
        responseDistanceMapsCall.enqueue(new Callback<ResponseDistanceMaps>() {
            @Override
            public void onResponse(Call<ResponseDistanceMaps> call, Response<ResponseDistanceMaps> response) {
                String status = response.body().getStatus();
                if (status.equals("OK")) {
                    List<RowsItem> rowsItem = response.body().getRows();
                    for (int a = 0; a < rowsItem.size(); a++) {
                        List<ElementsItem> elementsItem = rowsItem.get(a).getElements();
                        if (elementsItem.get(a).getStatus().equals("OK")) {
                            for (int b = 0; b < elementsItem.size(); b++) {

                                Distance distance = elementsItem.get(b).getDistance();
                                String jarak = distance.getText();
                                holder.tv_jarak.setText("Jarak : " + jarak);


                                Duration duration = elementsItem.get(b).getDuration();
                                String waktu = duration.getText();
//                                holder.tv.setText("Waktu : " + waktu);
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseDistanceMaps> call, Throwable t) {

                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.tv_nomor.setText(String.valueOf(position + 1));
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
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
        private RelativeLayout rl_continer_list;

        PesananListRecyclerClickListener pesananListRecyclerClickListener;


        public MyHolderView(@NonNull View itemView, PesananListRecyclerClickListener clickListener) {
            super(itemView);

            rl_lihat = itemView.findViewById(R.id.rl_lihat);
            tv_nomor = itemView.findViewById(R.id.tv_nomor);
            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_kode = itemView.findViewById(R.id.tv_kode);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);
            rl_continer_list = itemView.findViewById(R.id.rl_continer_list);

            pesananListRecyclerClickListener = clickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            pesananListRecyclerClickListener.onPesananClicked(getAdapterPosition());
        }
    }

    public interface PesananListRecyclerClickListener {
        void onPesananClicked(int position);
    }

}
