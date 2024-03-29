package com.example.licenta.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.Models.Investigation;
import com.example.licenta.R;

import java.util.ArrayList;

public class CheckoutAdapter extends ArrayAdapter<Investigation> {
    public CheckoutAdapter(@NonNull Context context, ArrayList<Investigation> investigations){
        super(context, 0, investigations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkout_item,parent,false);
        }

        TextView txtInvestigationNameCheckout = convertView.findViewById(R.id.txtInvestigationNameCheckout);
        TextView txtInvestigationSpecialtyCheckout = convertView.findViewById(R.id.txtInvestigationSpecialtyCheckout);
        TextView txtInvestigationPriceCheckout = convertView.findViewById(R.id.txtInvestigationPriceCheckout);

        Investigation investigation = getItem(position);

        txtInvestigationNameCheckout.setText(investigation.getName());
        txtInvestigationSpecialtyCheckout.setText(investigation.getSpecialty().getType().toString());
        txtInvestigationPriceCheckout.setText(String.valueOf(investigation.getPrice())+" Ron");

        return convertView;
    }
}
