package com.hujing.nanchangnews.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private ViewPager vp;
    private int[] picRes;
    private List<ImageView> imageViewList;
    private RelativeLayout rl;
    private MyAdapter myAdapter;
    private ImageView redPoint;
    private int lastPosition;
    private Button button;
    private RelativeLayout skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initUI();
        initData();
        setViewPagerAdapter();
        setOnClick();
    }





    private void setOnClick() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        SpUtils.setBoolean(this,true,SpUtils.IS_FIRST_USE);
    }

    private void initData() {
        picRes = new int[]{R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};
        imageViewList=new ArrayList<>();
        for (int i=0;i<picRes.length;i++){
            ImageView iv=new ImageView(this);
            Glide.with(this).load(picRes[i]).into(iv);
            imageViewList.add(iv);
            ImageView point =new ImageView(this);
            point.setBackgroundResource(R.drawable.gray_point);
            RelativeLayout.LayoutParams layoutParams =new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i!=0){
                layoutParams.leftMargin=80*i;
            }
            rl.addView(point,layoutParams);
        }
    }

    private void setViewPagerAdapter() {
        myAdapter = new MyAdapter();
        vp.setAdapter(myAdapter);
        redPoint = new ImageView(this);
        button.setVisibility(View.GONE);
        redPoint.setImageResource(R.drawable.red_point);
        rl.addView(redPoint);
        lastPosition = vp.getCurrentItem();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private ObjectAnimator translateAnimation;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position- lastPosition >0){
                    translateAnimation = ObjectAnimator.ofFloat(redPoint,"translationX",
                            redPoint.getTranslationX(),redPoint.getTranslationX()+80);



                }else if (position- lastPosition <0){
                    translateAnimation=ObjectAnimator.ofFloat(redPoint,"translationX",
                            redPoint.getTranslationX(),redPoint.getTranslationX()-80);

                }
                translateAnimation.setDuration(10);
                translateAnimation.start();
                lastPosition=position;
                if (position==imageViewList.size()-1){

                    button.setVisibility(View.VISIBLE);
                }else {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
    }

    private void initUI() {
        vp = (ViewPager) findViewById(R.id.vp);
        rl = (RelativeLayout) findViewById(R.id.rl_move);
        button = (Button) findViewById(R.id.button);
        skip = (RelativeLayout) findViewById(R.id.rl_skip);
    }
    class MyAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViewList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
