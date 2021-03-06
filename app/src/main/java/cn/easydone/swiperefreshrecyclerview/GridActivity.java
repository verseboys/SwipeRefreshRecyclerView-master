package cn.easydone.swiperefreshrecyclerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import cn.easydone.swiperefreshrecyclerview.to.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshrecyclerview.to.GridSpacingItemDecoration;
import cn.easydone.swiperefreshrecyclerview.to.HeaderViewRecyclerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Android Studio
 * User: Ailurus(ailurus@foxmail.com)
 * Date: 2015-11-06
 * Time: 11:33
 */
public class GridActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HeaderViewRecyclerAdapter stringAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spacingInPixels = 26;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));

        setData();
        RefreshAdapter refreshAdapter = new RefreshAdapter(mList, this);
        stringAdapter = new HeaderViewRecyclerAdapter(refreshAdapter);
        createLoadMoreView();
        recyclerView.setAdapter(stringAdapter);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((stringAdapter.getItemCount() - 1) == position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                simulateLoadMoreData();
            }
        });
    }

    private void simulateLoadMoreData() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        loadMoreData();
                        stringAdapter.notifyDataSetChanged();
                        Toast.makeText(GridActivity.this, "Load Finished!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();
    }

    private void loadMoreData() {
        List<String> moreList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            moreList.add("加载更多的数据");
        }
        mList.addAll(moreList);
    }

    private void setData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mList.add("第" + i + "个");
        }
    }

    @Override
    public void onRefresh() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        fetchingNewData();
                        swipeRefreshLayout.setRefreshing(false);
                        stringAdapter.notifyDataSetChanged();
                        Toast.makeText(GridActivity.this, "Refresh Finished!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();
    }

    private void fetchingNewData() {
        mList.add(0, "下拉刷新出来的数据");
        mList.add(0, "下拉刷新出来的数据");
    }

    private void createLoadMoreView() {
        View loadMoreView = LayoutInflater
                .from(GridActivity.this)
                .inflate(R.layout.view_load_more, recyclerView, false);
        stringAdapter.addFooterView(loadMoreView);
    }
}
