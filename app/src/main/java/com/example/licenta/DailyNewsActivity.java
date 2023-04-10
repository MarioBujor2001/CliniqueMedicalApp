package com.example.licenta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.licenta.Adapters.DailyNewsAdapter;
import com.example.licenta.Models.ForumPost;
import com.example.licenta.Utils.APICommunication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DailyNewsActivity extends AppCompatActivity {

    private RecyclerView recvDailyNews;
    private DailyNewsAdapter adapter;
    private List<ForumPost> news;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private CardView popUpCard;
    private ImageView imgNewsCoverPicture;
    private TextView txtNewsTitle;
    private EditText txtForumContent;
    private boolean isShowingPopup = false;
    private BroadcastReceiver forumPostsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                loadForumPosts();
                reloadForumPostsAdapter();
            }
        }
    };

    private BroadcastReceiver showMoreInfoPostReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSucces = intent.getBooleanExtra("success", false);
            if (isSucces) {
                ForumPost post = (ForumPost) intent.getSerializableExtra("post");
                if (post != null) {
                    displayMoreInfo(post);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_news);
        recvDailyNews = findViewById(R.id.recvDailyNews);
        popUpCard = findViewById(R.id.popUpCard);
        imgNewsCoverPicture = findViewById(R.id.imgNewsCoverPicture);
        txtNewsTitle = findViewById(R.id.txtNewsTitle);
        txtForumContent = findViewById(R.id.txtForumContent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(forumPostsReceiver,
                new IntentFilter("apiMessageForumPostsReceived"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(showMoreInfoPostReceiver,
                new IntentFilter("showMoreInfoPostReceiver"));
        APICommunication.getForumPosts(getApplicationContext());
    }

    public void reloadForumPostsAdapter() {
        adapter = new DailyNewsAdapter(getApplicationContext());
        adapter.setNews(news);
        recvDailyNews.setAdapter(adapter);
        recvDailyNews.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    public void loadForumPosts() {
        try {
            news = new ArrayList<>();
            for (int i = 0; i < APICommunication.forumPostsArray.length(); i++) {
                JSONObject currentForumPost = APICommunication.forumPostsArray.getJSONObject(i);
                ForumPost f = new ForumPost();
                f.setId(currentForumPost.getInt("id"));
                f.setContent(currentForumPost.getString("content"));
                f.setTitle(currentForumPost.getString("title"));
                f.setImgUrl(currentForumPost.getString("imgUrl"));
                news.add(f);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayMoreInfo(ForumPost post) {
        Log.i("TAG", "displayMoreInfo: reached");
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(post.getImgUrl())
                .into(imgNewsCoverPicture);
        txtNewsTitle.setText(post.getTitle());
        txtForumContent.setText(post.getContent());

        popUpCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(popUpCard.getVisibility() == View.VISIBLE){
            popUpCard.setVisibility(View.GONE);
        }else{
            finish();
        }
    }
}