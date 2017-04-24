package com.hujing.nanchangnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.bean.SqlNewsData;

import java.io.File;
import java.util.List;

/**
 * Created by acer on 2017/4/21.
 */

public class MyBaseQuickAdapter extends BaseItemDraggableAdapter<SqlNewsData,BaseViewHolder> {

    public MyBaseQuickAdapter(int layoutResId, List<SqlNewsData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SqlNewsData item) {
        helper.setText(R.id.tv_news_title,item.getTitle())
                .setText(R.id.tv_news_date,item.getDate())
                .setText(R.id.tv_news_author,"来源: "+item.getAuthor());
        Glide.with(mContext).load(new File(item.getFilePath())).
                into((ImageView) helper.getView(R.id.news_iv));
    }
}
