package com.baadalletta.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baadalletta.app.R;

public class RiwayatKurirAdapter extends RecyclerView.Adapter<RiwayatKurirAdapter.MyHolderView> {

    private Context context;

    public RiwayatKurirAdapter(Context context) {
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return 14;
    }

    public class MyHolderView extends RecyclerView.ViewHolder {
        public MyHolderView(@NonNull View itemView) {
            super(itemView);
        }
    }
}
