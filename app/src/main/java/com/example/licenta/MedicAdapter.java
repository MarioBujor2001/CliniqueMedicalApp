package com.example.licenta;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Medic;

import java.util.ArrayList;

public class MedicAdapter extends RecyclerView.Adapter<MedicAdapter.ViewHolder> {

    private ArrayList<Medic> medici = new ArrayList<>();
    private Context ctx;

    public MedicAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public MedicAdapter() {
    }

    public ArrayList<Medic> getMedici() {
        return medici;
    }

    public void setMedici(ArrayList<Medic> medici) {
        this.medici = medici;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(ctx)
                .asBitmap()
                .load(medici.get(position).getPhoto())
                .into(holder.imgMedicProfile);
        holder.txtMedicName.setText(medici.get(position).getFirstName() + " " + medici.get(position).getLastName());
        if(medici.get(position).getSpecialitati().size()>0 && medici.get(position).getSpecialitati().get(0)!=null){
            holder.txtMedicSpec.setText(medici.get(position).getSpecialitati().get(0).getTip().toString().toUpperCase());
        }
//        holder.txtMedicSpec.setText(medici.get(position).getSpecialitati().get(0).getTip().toString().toUpperCase());
        holder.txtMedicRating.setText("⭐"+medici.get(position).getRating().toString());
        Log.i("debug", "am ajuns aici");
    }

    @Override
    public int getItemCount() {
        return this.medici.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMedicProfile;
        private TextView txtMedicName, txtMedicSpec, txtMedicRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedicProfile = itemView.findViewById(R.id.imgMedicProfile);
            txtMedicName = itemView.findViewById(R.id.txtMedicName);
            txtMedicSpec = itemView.findViewById(R.id.txtMedicSpec);
            txtMedicRating = itemView.findViewById(R.id.txtMedicRating);
        }
    }
}
