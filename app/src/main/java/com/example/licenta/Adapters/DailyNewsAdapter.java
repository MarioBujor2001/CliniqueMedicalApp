package com.example.licenta.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.licenta.Models.ForumPost;
import com.example.licenta.R;

import java.util.ArrayList;
import java.util.List;

public class DailyNewsAdapter extends RecyclerView.Adapter<DailyNewsAdapter.ViewHolder> {

    private List<ForumPost> news = new ArrayList<>();
    private Context ctx;

    public DailyNewsAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public List<ForumPost> getNews() {
        return news;
    }

    public void setNews(List<ForumPost> news) {
        this.news = news;
    }

    @NonNull
    @Override
    public DailyNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_news_item, parent, false);
        return new DailyNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyNewsAdapter.ViewHolder holder, int position) {
        ForumPost dailyNews = news.get(position);
        Glide.with(ctx)
                .asBitmap()
                .load(news.get(position).getImgUrl())
                .into(holder.imgNewsCoverPicture);
        holder.txtNewsTitle.setText(news.get(position).getTitle());
        holder.btnSeeMoreNews.setOnClickListener(view -> {
            Intent intent = new Intent("showMoreInfoPostReceiver");
            intent.putExtra("success", true);
            intent.putExtra("post", dailyNews);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNewsTitle;
        private Button btnSeeMoreNews;
        private ImageView imgNewsCoverPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNewsTitle = itemView.findViewById(R.id.txtNewsTitle);
            imgNewsCoverPicture = itemView.findViewById(R.id.imgNewsCoverPicture);
            btnSeeMoreNews = itemView.findViewById(R.id.btnSeeMoreNews);
        }
    }
}
