package com.hujing.nanchangnews.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hujing.nanchangnews.Gson.News.NewsData;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.adapter.NewsRecyclerViewAdapter;
import com.hujing.nanchangnews.bean.SqlNewsData;
import com.hujing.nanchangnews.utils.HttpUtils;
import com.hujing.nanchangnews.utils.NetWorkUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {

    private static final String PATH = "path";
    private WebView wv;
    private NewsData newsData;
    private FloatingActionButton fab;
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private int selectedItem;
    private Bitmap bitmap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Bundle bundle = msg.getData();
                    String path = bundle.getString(PATH);
                    SqlNewsData data = new SqlNewsData();
                    data.setAuthor(newsData.getAuthor());
                    data.setFilePath(path);
                    data.setTitle(newsData.getTitle());
                    data.setUniquekey(newsData.getUniquekey());
                    data.setDate(newsData.getDate());
                    data.setUrl(newsData.getUrl());
                    data.save();
                    Toast.makeText(NewsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(NewsActivity.this, "收藏失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private boolean isCollected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ShareSDK.initSDK(this, "1d1253a2ed6ce");
        initUI();
        initData();
        setWebView();
        setFloatingActionButtonVisible();
        setFloatingActionButton();

    }

    private void setFloatingActionButtonVisible() {
        boolean isConnected = NetWorkUtils.isNetworkConnected(this);
        boolean empty = TextUtils.isEmpty(newsData.getPic1());
        if (isConnected&&empty==false) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void setFloatingActionButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "是否收藏此新闻？", Snackbar.LENGTH_SHORT).setAction("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCollected = false;
                        DataSupport.findAllAsync(SqlNewsData.class).listen(new FindMultiCallback() {
                            @Override
                            public <T> void onFinish(List<T> t) {
                                List<SqlNewsData> dataList = (List<SqlNewsData>) t;
                                for (SqlNewsData data : dataList) {
                                    if (data.getUniquekey().equals(newsData.getUniquekey())) {
                                        isCollected = true;
                                        break;
                                    }
                                }
                                if (isCollected) {
                                    Toast.makeText(NewsActivity.this, "新闻已经收藏过了", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    restorePic();
                                }
                            }
                        });

                    }
                }).show();
            }
        });
    }

    private void restorePic() {
        final String pic1 = newsData.getPic1();
            new Thread() {
                @Override
                public void run() {
                    HttpUtils.sendOkHttpRequest(pic1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = Message.obtain();
                            message.what = FAILED;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String path = restore(response);
                            Message message = Message.obtain();
                            message.what = SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString(PATH, path);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    });
                }
            }.start();


    }

    private String restore(Response response) {
        InputStream inputStream = response.body().byteStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        String name = newsData.getUniquekey() + ".jpg";
        String path = this.getFilesDir().getAbsolutePath() + File.separator + name;
        File file = new File(path);
        FileOutputStream fos = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return path;
    }

    private void initData() {
        Intent intent = getIntent();
        newsData = (NewsData) intent.getSerializableExtra(NewsRecyclerViewAdapter.NEWS_DATA);
    }

    private void setWebView() {
        wv.getSettings().setJavaScriptEnabled(false);
        selectedItem = SpUtils.getInt(NewsActivity.this, 2, SpUtils.SELECT_ITEM);
        switch (selectedItem) {
            case 0:
                setFontSize(200);

                break;
            case 1:
                setFontSize(150);

                break;
            case 2:
                setFontSize(100);

                break;
            case 3:
                setFontSize(75);

                break;
            case 4:
                setFontSize(50);

                break;
        }
        wv.setWebViewClient(new WebViewClient());
        wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        wv.loadUrl(newsData.getUrl());
    }

    private void initUI() {
        wv = (WebView) findViewById(R.id.wv);
        Toolbar webView_toolbar = (Toolbar) findViewById(R.id.webView_toolbar);
        setSupportActionBar(webView_toolbar);
        ActionBar actionBar = getSupportActionBar();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                showShare();
                break;
            case R.id.fonts:
                showFontSize();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFontSize() {

        final String[] fontSizes = new String[]{"超大字体", "大字体", "正常字体", "小字体", "超小字体"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置字体")
                .setSingleChoiceItems(fontSizes, selectedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItem = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedItem) {
                            case 0:
                                setFontSize(200);

                                break;
                            case 1:
                                setFontSize(150);

                                break;
                            case 2:
                                setFontSize(100);

                                break;
                            case 3:
                                setFontSize(75);

                                break;
                            case 4:
                                setFontSize(50);

                                break;
                        }
                        SpUtils.setInt(NewsActivity.this, selectedItem, SpUtils.SELECT_ITEM);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setFontSize(int size) {
        wv.getSettings().setTextZoom(size);
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();//关闭sso授权
        oks.disableSSOWhenAuthorize();
// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("新闻分享");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(newsData.getUrl());
// text是分享文本，所有平台都需要这个字段
        oks.setText(newsData.getTitle());
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(newsData.getUrl());
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这条新闻十分有趣，快来看看吧！！");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(newsData.getUrl());
        if (!TextUtils.isEmpty(newsData.getPic1())) {
            oks.setImageUrl(newsData.getPic1());
        }
        oks.setTheme(OnekeyShareTheme.fromValue(2));
// 启动分享GUI
        oks.show(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }
}
