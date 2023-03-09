package com.example.licenta.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.Medic;
import com.example.licenta.Models.Programare;
import com.example.licenta.R;
import com.example.licenta.RecyclerViewInterface;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProgramareAdapter extends RecyclerView.Adapter<ProgramareAdapter.ViewHolder> {

    private ArrayList<Programare> programari = new ArrayList<>();
    private Context ctx;

    public ProgramareAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public ArrayList<Programare> getProgramari() {
        return programari;
    }

    public void setProgramari(ArrayList<Programare> programari) {
        this.programari = programari;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ProgramareAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medic medic = programari.get(position).getMedic();
        Glide.with(ctx)
                .asBitmap()
                .load(medic.getPhoto())
                .into(holder.imgMedicProfile);
        holder.txtMedicName.setText("Dr. "+medic.getFirstName() + " " + medic.getLastName());
        String spec = medic.getSpecialitate().getTip().toString();
        holder.txtMedicSpec.setText(spec.substring(0,1).toUpperCase() + spec.substring(1));
//        holder.txtMedicSpec.setText(medici.get(position).getSpecialitati().get(0).getTip().toString().toUpperCase());
        holder.txtMedicRating.setText("⭐"+medic.getRating().toString());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String dateFormated = programari.get(position).getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            holder.txtData.setText(dateFormated.substring(0,10));
            holder.txtOra.setText(dateFormated.substring(11));
        }
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("progAdapterDelete");
                intent.putExtra("toDelete", programari.get(position));
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return programari.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgMedicProfile;
        private TextView txtMedicName, txtMedicSpec, txtMedicRating, txtData, txtOra;
        private CardView parent;
        private Button btnCancel, btnReschedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedicProfile = itemView.findViewById(R.id.imgMedicProfile);
            txtMedicName = itemView.findViewById(R.id.txtMedicName);
            txtMedicSpec = itemView.findViewById(R.id.txtMedicSpec);
            txtMedicRating = itemView.findViewById(R.id.txtMedicRating);
            txtData = itemView.findViewById(R.id.tvAppDate);
            txtOra = itemView.findViewById(R.id.tvAppTime);
            btnCancel = itemView.findViewById(R.id.btnAppCancel);
            btnReschedule = itemView.findViewById(R.id.btnAppReschedule);
            parent = itemView.findViewById(R.id.parentCardMedic);
        }
    }
}
