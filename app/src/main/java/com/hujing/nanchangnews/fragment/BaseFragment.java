package com.hujing.nanchangnews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hujing.nanchangnews.Gson.News.News;
import com.hujing.nanchangnews.Gson.News.NewsData;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.adapter.NewsRecyclerViewAdapter;
import com.hujing.nanchangnews.utils.HttpUtils;
import com.hujing.nanchangnews.utils.JSONUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by acer on 2017/4/12.
 */

public class BaseFragment extends Fragment {
    private static final int FIRST_LOAD_FAILED = 4;
    private static boolean first_load_failed;

    public BaseFragment() {
    }

    private String mParam;

    public static BaseFragment newInstance(String param) {
        BaseFragment fragment = new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL, param);
        fragment.setArguments(bundle);
        return fragment;
    }

    private Context mContext;
    private final int FIRST_LOAD = 1;
    private final int REFRESH = 2;
    private final int FAILED = 3;
    private static final String URL = "url";
    private String url;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            layoutManager = new LinearLayoutManager(mContext);
            switch (msg.what) {
                case FIRST_LOAD:
                    srl.setRefreshing(false);
                    adapter = new NewsRecyclerViewAdapter(mContext, dataList);
                    rv.setLayoutManager(layoutManager);
                    rv.setAdapter(adapter);
                    break;
                case REFRESH:

                    Toast.makeText(mContext, "刷新成功,为您新增" + moreDataList.size() + "条新闻", Toast.LENGTH_SHORT).show();
                    if (adapter != null) {
                        srl.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case FIRST_LOAD_FAILED:
                    adapter = new NewsRecyclerViewAdapter(mContext, dataList);

                    rv.setLayoutManager(layoutManager);
                    rv.setAdapter(adapter);
                    Toast.makeText(mContext, "无网络连接", Toast.LENGTH_SHORT).show();
                    srl.setRefreshing(false);
                    break;
                case FAILED:
                    Toast.makeText(mContext, "无网络连接", Toast.LENGTH_SHORT).show();
                    srl.setRefreshing(false);
                default:
                    break;
            }

        }
    };
    private View view;
    private static final String TAG = "BaseFragment";
    private SwipeRefreshLayout srl;
    private NewsRecyclerViewAdapter adapter;
    private List<NewsData> dataList;
    private News news;
    private List<NewsData> moreDataList = new ArrayList<>();
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(URL);
        }
        mContext = getActivity();
        dataList = new ArrayList<>();
        url = "http://v.juhe.cn/toutiao/index?type=" + mParam + "&key=51b2b10b9d24d149dceeb79e72e4edf2";
        initDataFromInternet();

    }

    private void initDataFromInternet() {
        new Thread() {
            @Override
            public void run() {
                HttpUtils.sendOkHttpRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        firstRequestFailed();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string.contains("uniquekey")) {
                            SpUtils.setString(mContext, string, mParam);
                            news = JSONUtils.parseNewsJSONWithGson(string);
                            dataList.addAll(news.result.dataList);
                            Message message = Message.obtain();
                            message.what = FIRST_LOAD;
                            handler.sendMessage(message);
                        } else {
                            firstRequestFailed();
                        }
                    }
                });
            }
        }.start();
    }

    private void requestFailed() {
        Message message = Message.obtain();
        message.what = FAILED;
        handler.sendMessage(message);
    }

    private void firstRequestFailed() {
        first_load_failed = true;
        String json = SpUtils.getString(mContext, mParam);
        if (json != null) {
            news = JSONUtils.parseNewsJSONWithGson(json);
            dataList.addAll(news.result.dataList);
        }
        Message message = Message.obtain();
        message.what = FIRST_LOAD_FAILED;
        handler.sendMessage(message);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_fragment, container, false);
        initUI();
        setRefresh();

        return view;
    }

    private void setRefresh() {
        srl.setColorSchemeResources(R.color.colorPrimary);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    public void refreshData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HttpUtils.sendOkHttpRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        requestFailed();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string.contains("uniquekey")) {
                            if (first_load_failed) {
                                first_load_failed = false;
                            }
                            int flag;

                            SpUtils.setString(mContext, string, mParam);
                            News news = JSONUtils.parseNewsJSONWithGson(string);
                            moreDataList.clear();
                            List<String> stringList = new ArrayList<>();
                            for (NewsData data : news.result.dataList) {
                                flag = 0;
                                for (NewsData nData : dataList) {
                                    stringList.add(nData.getUniquekey());
                                }
                                for (String unq : stringList) {
                                    if (unq.equals(data.getUniquekey())) {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    moreDataList.add(data);
                                }
                            }
                            dataList.addAll(0, moreDataList);
                            Message message = Message.obtain();
                            message.what = REFRESH;
                            handler.sendMessage(message);
                        }else {
                            requestFailed();
                        }

                    }
                });
            }
        }.start();
    }

    private void initUI() {
        srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        srl.setRefreshing(true);
        rv = (RecyclerView) view.findViewById(R.id.rv);
    }

}
