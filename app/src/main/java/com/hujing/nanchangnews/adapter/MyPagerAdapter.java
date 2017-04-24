package com.hujing.nanchangnews.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.activity.CollectionOfNewsActivity;
import com.hujing.nanchangnews.bean.SqlNewsData;

import java.io.File;
import java.util.List;

/**
 * Created by acer on 2017/4/20.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<SqlNewsData>  mNewsDataList;
    private Context mContext;
    public MyPagerAdapter(Context context,List<SqlNewsData> newsDataList) {
        mContext=context;
        mNewsDataList=newsDataList;
    }

    @Override
    public int getCount() {
        if (mNewsDataList.size()<3){
            return mNewsDataList.size();
        }else {
            return 3;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView=new ImageView(mContext);
            String filePath = mNewsDataList.get(position).getFilePath();
            File file=new File(filePath);
            Glide.with(mContext).load(file).centerCrop().into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CollectionOfNewsActivity activity= (CollectionOfNewsActivity) mContext;
                    activity.startNewsActivity(mNewsDataList,position);
                }
            });
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
