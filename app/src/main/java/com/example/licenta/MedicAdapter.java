package com.example.licenta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Medic;

import java.util.ArrayList;

public class MedicAdapter extends RecyclerView.Adapter<MedicAdapter.ViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private ArrayList<Medic> medici = new ArrayList<>();
    private Context ctx;

    public MedicAdapter(Context ctx, RecyclerViewInterface r) {
        this.ctx = ctx;
        this.recyclerViewInterface = r;
    }

    public MedicAdapter(RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
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
        holder.txtMedicName.setText("Dr. "+medici.get(position).getFirstName() + " " + medici.get(position).getLastName());
        if(medici.get(position).getSpecialitati().size()>0 && medici.get(position).getSpecialitati().get(0)!=null){
            holder.txtMedicSpec.setText(medici.get(position).getSpecialitati().get(0).getTip().toString().toUpperCase());
        }
//        holder.txtMedicSpec.setText(medici.get(position).getSpecialitati().get(0).getTip().toString().toUpperCase());
        holder.txtMedicRating.setText("⭐"+medici.get(position).getRating().toString());
    }

    @Override
    public int getItemCount() {
        return this.medici.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMedicProfile;
        private TextView txtMedicName, txtMedicSpec, txtMedicRating;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedicProfile = itemView.findViewById(R.id.imgMedicProfile);
            txtMedicName = itemView.findViewById(R.id.txtMedicName);
            txtMedicSpec = itemView.findViewById(R.id.txtMedicSpec);
            txtMedicRating = itemView.findViewById(R.id.txtMedicRating);
            parent = itemView.findViewById(R.id.parentCardMedic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
