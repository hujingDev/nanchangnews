package com.hujing.nanchangnews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.utils.DataBaseUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SplashActivity extends AppCompatActivity  {

    private RelativeLayout splash;
    private AnimationSet set;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        initAnim();
        moveToNextActivity();
        initDatabase();
    }

    private void initDatabase() {
        Connector.getDatabase();
        copyDatabase("AllCities.db");
    }


    private void copyDatabase( String dbName ) {
        File file=new File(getFilesDir(),dbName);
        if (file.exists()){
            return;
        }
        InputStream stream=null;
        FileOutputStream fos=null;
        try {
            stream = getAssets().open(dbName);
            fos =new FileOutputStream(file);
            int temp=-1;
            byte[] buffer=new byte[1024];
            while ((temp=stream.read(buffer))!=-1){
                fos.write(buffer,0,temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos!=null&&stream!=null){
                try {
                    fos.close();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveToNextActivity() {
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            if (SpUtils.getBoolean(SplashActivity.this, false, SpUtils.IS_FIRST_USE)) {
                                //不是第一次用
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                //是第一次用
                                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                                finish();

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initAnim() {
        RotateAnimation ra = new RotateAnimation(0, 369,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setFillAfter(true);
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(1000);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(1000);
        aa.setFillAfter(true);
        set = new AnimationSet(true);
        set.addAnimation(aa);
        set.addAnimation(sa);
        set.addAnimation(ra);
        splash.startAnimation(set);
    }

    private void initUI() {
        splash = (RelativeLayout) findViewById(R.id.activity_splash);
    }

}
