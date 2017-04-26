package com.virtualassistant.LoggedIn.News;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.virtualassistant.Constants;
import com.virtualassistant.R;
import com.virtualassistant.models.News;

import java.util.List;


public class NewsItemRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemRecyclerViewAdapter.ViewHolder> {

    private final List<News> mValues;
    private Context context;

    public NewsItemRecyclerViewAdapter(List<News> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.title.setText(mValues.get(position).getTitle());
        holder.descriptionTV.setText(mValues.get(position).getDescription());

        ImageLoader.getInstance().displayImage(mValues.get(position).getImageURL(), holder.newsIV);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WebViewActivity.class);
                i.putExtra(Constants.KEY_NEWS_STRING, mValues.get(position).getUrl());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView descriptionTV;
        public final ImageView newsIV;
        public News mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title_textView);
            descriptionTV = (TextView) view.findViewById(R.id.textView_description);
            newsIV = (ImageView) view.findViewById(R.id.imageView_news_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + descriptionTV.getText() + "'";
        }
    }
}
