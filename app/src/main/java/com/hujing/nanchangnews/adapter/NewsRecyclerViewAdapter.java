package com.hujing.nanchangnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hujing.nanchangnews.Gson.News.NewsData;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.activity.NewsActivity;

import java.util.List;

/**
 * Created by acer on 2017/4/14.
 */

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {
    public static final String NEWS_DATA = "newsData";
    private Context mContext;
    private List<NewsData> mNewsDataList;

    public NewsRecyclerViewAdapter(Context context, List<NewsData> newsDataList) {
        mContext = context;
        mNewsDataList = newsDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, NewsActivity.class);
                int position=viewHolder.getAdapterPosition();
                NewsData newsData = mNewsDataList.get(position);
                intent.putExtra(NEWS_DATA,newsData);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mNewsDataList.get(position).pic1)
                .placeholder(R.drawable.loading)
                .crossFade()
                .fitCenter()
                .error(R.drawable.error)
                .into(holder.news_iv);
        holder.tv_news_title.setText(mNewsDataList.get(position).title);
        holder.tv_news_author.setText("来源: " + mNewsDataList.get(position).author);
        holder.tv_news_date.setText(mNewsDataList.get(position).date);
    }

    @Override
    public int getItemCount() {
        return mNewsDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView news_iv;
        CardView cardView;
        TextView tv_news_title;
        TextView tv_news_author;
        TextView tv_news_date;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            news_iv = (ImageView) itemView.findViewById(R.id.news_iv);
            tv_news_title = (TextView) itemView.findViewById(R.id.tv_news_title);
            tv_news_author = (TextView) itemView.findViewById(R.id.tv_news_author);
            tv_news_date = (TextView) itemView.findViewById(R.id.tv_news_date);
        }
    }
}
