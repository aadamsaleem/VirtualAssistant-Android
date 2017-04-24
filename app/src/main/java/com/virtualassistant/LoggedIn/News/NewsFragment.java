package com.virtualassistant.LoggedIn.News;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.virtualassistant.R;
import com.virtualassistant.client.CompletionInterface;
import com.virtualassistant.client.NewsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    public NewsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_item_list, container, false);

        final Context context = view.getContext();

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

        final ArrayList<News> newsList = new ArrayList<>();

        NewsManager.getArticles(getContext(), new CompletionInterface() {
            @Override
            public void onSuccess(JSONObject result) {

                JSONArray articlesArray = null;
                try {
                    articlesArray = result.getJSONArray("articles");


                    for (int i = 0; i < articlesArray.length(); i++) {
                        JSONObject article = articlesArray.getJSONObject(i);
                        News item = new News();
                        item.setTitle(article.getString("title"));
                        item.setDescription(article.getString("description"));
                        item.setUrl(article.getString("url"));
                        item.setImageURL(article.getString("urlToImage"));

                        newsList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerView.setAdapter(new NewsItemRecyclerViewAdapter(newsList, context));
            }

            @Override
            public void onFailure() {
                Log.e("FAIL", "failed to get articles");

            }
        });


        return view;
    }
}
