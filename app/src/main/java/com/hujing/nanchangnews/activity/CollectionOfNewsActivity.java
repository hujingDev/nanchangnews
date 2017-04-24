package com.hujing.nanchangnews.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.hujing.nanchangnews.Gson.News.NewsData;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.adapter.MyBaseQuickAdapter;
import com.hujing.nanchangnews.adapter.MyPagerAdapter;
import com.hujing.nanchangnews.adapter.NewsRecyclerViewAdapter;
import com.hujing.nanchangnews.bean.SqlNewsData;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionOfNewsActivity extends AppCompatActivity {
    private static final String TAG = "CollectionOfNewsActivit";
    private ViewPager vp;
    private List<SqlNewsData> dataList=new ArrayList<>();
    private Toolbar toolbar;
    private MyBaseQuickAdapter myDraggableAdapter;
    private RecyclerView rv;
    private int lastPosition;
    private boolean isNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_of_news);
        initData();
        initUI();

    }

    private void initData() {
        DataSupport.order("id desc").findAsync(SqlNewsData.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                List<SqlNewsData> list= (List<SqlNewsData>) t;
                dataList.addAll(list);
                initRecyclerViewAdapter(dataList);
                setRecyclerView();
                setRecyclerViewAdapter();
            }
        });

    }

    private void setRecyclerViewAdapter() {
        myDraggableAdapter.setEmptyView(R.layout.empty_view, (ViewGroup) rv.getParent());
        myDraggableAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startNewsActivity(dataList,position);

            }
        });
        View view1 = getView(R.layout.collect_header_view);
        myDraggableAdapter.addHeaderView(view1,0);
        View view2=getView(R.layout.collect_header_view2);
        myDraggableAdapter.addHeaderView(view2,1);
        ItemDragAndSwipeCallback callBack=new ItemDragAndSwipeCallback(myDraggableAdapter);
        ItemTouchHelper helper=new ItemTouchHelper(callBack);
        helper.attachToRecyclerView(rv);
        myDraggableAdapter.enableSwipeItem();
        myDraggableAdapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                dataList.get(pos).delete();
                deleteMyFile(dataList.get(pos).getFilePath());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });
//       myDraggableAdapter.enableDragItem();
        setPagerView(view1);
    }

    private boolean deleteMyFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile()&&file.exists()){
            return file.delete();
        }else {
            return false;
        }
    }

    public void startNewsActivity(List<SqlNewsData> dataList,int position) {
        Intent intent = new Intent(CollectionOfNewsActivity.this,NewsActivity.class);
        SqlNewsData sqlNewsData = dataList.get(position);
        NewsData data=new NewsData();
        data.setAuthor(sqlNewsData.getAuthor());
        data.setUrl(sqlNewsData.getUrl());
        data.setTitle(sqlNewsData.getTitle());
        data.setUniquekey(sqlNewsData.getUniquekey());
        data.setDate(sqlNewsData.getDate());
        intent.putExtra(NewsRecyclerViewAdapter.NEWS_DATA,data);
        startActivity(intent);
    }


    private void setPagerView(View view) {
        ViewPager vp= (ViewPager) view.findViewById(R.id.rv_header_vp);
        TextView topTitle= (TextView) view.findViewById(R.id.top_title);
        List<SqlNewsData> pagerList=new ArrayList<>();
        for (int i=0;i<dataList.size();i++){
            pagerList.add(dataList.get(i));
        }
        if (pagerList.size()!=0) {
            topTitle.setText(pagerList.get(0).getTitle());
        }
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this, pagerList);
        vp.setAdapter(pagerAdapter);
        RelativeLayout rl= (RelativeLayout) view.findViewById(R.id.collect_rl_scroll);
        setViewPagerScroll(vp,rl,topTitle,pagerList);
    }

    private void setViewPagerScroll(ViewPager vp, final RelativeLayout rl, final TextView topTitle,
                                    final List<SqlNewsData> pagerList) {
        if (pagerList.size()==2){
            for (int i=0;i<2;i++){
                ImageView point=new ImageView(this);
                point.setImageResource(R.drawable.gray_point);
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.
                        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i==1){
                    layoutParams.leftMargin=80;
                }
                rl.addView(point,layoutParams);
                Log.d(TAG, "setPagerView: if");
            }
        }else if (pagerList.size()!=1){
            Log.d(TAG, "setPagerView: else");
            for (int i=0;i<3;i++){
                ImageView point=new ImageView(this);
                point.setImageResource(R.drawable.gray_point);
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.
                        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i!=0){
                    layoutParams.leftMargin=80*i;
                }
                rl.addView(point,layoutParams);
            }
        }
        final ImageView redPoint=new ImageView(this);
        redPoint.setImageResource(R.drawable.red_point);
        if (pagerList.size()!=1) {
            rl.addView(redPoint);
        }
        lastPosition = vp.getCurrentItem();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private ObjectAnimator translateAnimation;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                topTitle.setText(pagerList.get(position).getTitle());
                if (position- lastPosition >0){
                    translateAnimation = ObjectAnimator.ofFloat(redPoint,"translationX",
                            redPoint.getTranslationX(),redPoint.getTranslationX()+80);



                }else if (position- lastPosition <0){
                    translateAnimation=ObjectAnimator.ofFloat(redPoint,"translationX",
                            redPoint.getTranslationX(),redPoint.getTranslationX()-80);
                }
                translateAnimation.setDuration(10);
                translateAnimation.start();
                lastPosition =position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private View getView(int layoutRes) {
        View view=View.inflate(this,layoutRes,null);
        return view;
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(myDraggableAdapter);

    }


    private void initRecyclerViewAdapter(List<SqlNewsData> dataList) {
        myDraggableAdapter = new MyBaseQuickAdapter(R.layout.news_item, dataList);
    }


    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.collect_toolbar);
        rv = (RecyclerView) findViewById(R.id.collect_rv);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.del:
                if (dataList.size()>0){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("请问是否清空收藏？")
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (SqlNewsData data:dataList){
                                deleteMyFile(data.getFilePath());
                            }
                            dataList.clear();
                            DataSupport.deleteAll(SqlNewsData.class);
                            myDraggableAdapter.notifyDataSetChanged();

                        }
                    }).show();
                }else {
                    Toast.makeText(this, "没有收藏的新闻", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collect_menu,menu);
        return true;
    }
}
