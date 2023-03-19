package com.example.licenta.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.Models.Investigation;
import com.example.licenta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class InvestigationAdapter extends RecyclerView.Adapter<InvestigationAdapter.ViewHolder> {

    private ArrayList<Investigation> investigations = new ArrayList<>();
    private Context ctx;

    public InvestigationAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public ArrayList<Investigation> getInvestigations() {
        return investigations;
    }

    public void setInvestigations(ArrayList<Investigation> investigations) {
        this.investigations = investigations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.investigation_item, parent, false);
        return new InvestigationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestigationAdapter.ViewHolder holder, int position) {
        Investigation investigation = investigations.get(position);
        holder.txtInvestigationSpecialty.setText(investigation.getSpecialty().getTip().toString());
        holder.txtInvestigationPrice.setText(String.valueOf(investigation.getPrice()) + " Ron");
        holder.txtInvestigationName.setText(String.valueOf(investigation.getName()));
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("updateCart");
                intent.putExtra("value", investigation.getPrice());
                intent.putExtra("investigation", investigation);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                Log.i("broadcast", "sent");
            }
        });
    }

    @Override
    public int getItemCount() {
        return investigations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtInvestigationSpecialty, txtInvestigationPrice, txtInvestigationName;
        private FloatingActionButton btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInvestigationName = itemView.findViewById(R.id.txtInvestigationName);
            txtInvestigationPrice = itemView.findViewById(R.id.txtInvestigationPrice);
            txtInvestigationSpecialty = itemView.findViewById(R.id.txtInvestigationSpecialty);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
