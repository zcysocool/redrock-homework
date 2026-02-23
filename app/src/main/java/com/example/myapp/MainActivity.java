package com.example.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

// 新闻列表页面，主Activity
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Article> articleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 网络请求
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        api.getArticleList(0).enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    articleList = response.body().data.datas;
                    recyclerView.setAdapter(new MyAdapter());
                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "网络加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 适配器
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            Article article = articleList.get(position);
            holder.title.setText(article.title);
            holder.info.setText(article.author + " " + article.niceDate);
        }

        @Override
        public int getItemCount() {
            return articleList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView title, info;
            public MyHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                info = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    interface ApiService {
        @GET("article/list/{page}/json")
        Call<ResultData> getArticleList(@Path("page") int page);
    }

    class ResultData {
        public int errorCode;
        public Data data;
    }

    class Data {
        public List<Article> datas;
    }

    class Article {
        public String title;
        public String author;
        public String niceDate;
        public String link;
    }
}
