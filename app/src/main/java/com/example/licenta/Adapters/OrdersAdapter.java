package com.example.licenta.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta.Models.Investigation;
import com.example.licenta.Models.Order;
import com.example.licenta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private Context ctx;

    public OrdersAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtOrderNo.setText("#"+order.getId());
        float total = 0;
        for(Investigation i: order.getInvestigations()){
            total+=i.getPrice();
        }
        holder.txtOrderPrice.setText(total+" Ron");
        holder.btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("receiveMoreInfoOrder");
                intent.putExtra("success", true);
                intent.putExtra("orderId", order.getId());
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtOrderNo, txtOrderPrice;
        private Button btnMoreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNo = itemView.findViewById(R.id.txtOrderNo);
            txtOrderPrice = itemView.findViewById(R.id.txtOrderPrice);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }
    }
}
