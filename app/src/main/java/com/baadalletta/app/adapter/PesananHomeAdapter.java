package com.baadalletta.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.R;

public class PesananHomeAdapter extends RecyclerView.Adapter<PesananHomeAdapter.MyHolderView> {

    private Context context;

    public PesananHomeAdapter(Context context) {
        this.context = context;
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

        holder.tv_nomor.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyHolderView extends RecyclerView.ViewHolder {

        private TextView tv_nomor;

        public MyHolderView(@NonNull View itemView) {
            super(itemView);

            tv_nomor = itemView.findViewById(R.id.tv_nomor);

        }
    }
}
