package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.licenta.Adapters.DailyNewsAdapter;
import com.example.licenta.Adapters.OrdersAdapter;
import com.example.licenta.Models.DailyNews;

import java.util.ArrayList;
import java.util.List;

public class DailyNewsActivity extends AppCompatActivity {

    private RecyclerView recvDailyNews;
    private DailyNewsAdapter adapter;
    private List<DailyNews> news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_news);
        recvDailyNews = findViewById(R.id.recvDailyNews);
        news = new ArrayList<>();
        news.add(new DailyNews(1,"https://www.cdc.gov/nchs/images/hus/homepage-tiles/Health-risk-factors-800x500.png?_=27858","Risk Factors1","ceva"));
        news.add(new DailyNews(1,"https://www.cdc.gov/nchs/images/hus/homepage-tiles/Health-risk-factors-800x500.png?_=27858","Risk Factors1","ceva"));
        news.add(new DailyNews(1,"https://www.cdc.gov/nchs/images/hus/homepage-tiles/Health-risk-factors-800x500.png?_=27858","Risk Factors1","ceva"));
        reloadOrdersAdapter();
    }

    public void reloadOrdersAdapter() {
        adapter = new DailyNewsAdapter(this);
        adapter.setNews(news);
        recvDailyNews.setAdapter(adapter);
        recvDailyNews.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
}