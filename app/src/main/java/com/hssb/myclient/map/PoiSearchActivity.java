package com.hssb.myclient.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.hssb.myclient.R;
import com.hssb.myclient.adapter.CommonRecyclerAdapter;
import com.hssb.myclient.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchActivity extends AppCompatActivity implements View.OnClickListener, OnGetPoiSearchResultListener {

    private View viewSearch;
    private View viewCancel;
    private EditText etMessage;
    private RecyclerView recyclerView;
    private CommonRecyclerAdapter adapter;
    private List mList = new ArrayList();

    private int loadIndex = 0;
    private PoiSearch mPoiSearch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_search_act);
        initUI();
        initPoi();
    }

    @Override
    protected void onDestroy() {
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        super.onDestroy();
    }

    private void initPoi() {
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initUI() {
        viewSearch = findViewById(R.id.search);
        viewCancel = findViewById(R.id.cancel);
        etMessage = findViewById(R.id.et_message);
        viewSearch.setOnClickListener(this);
        viewCancel.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommonRecyclerAdapter<PoiInfo>(this, mList, R.layout.poi_search_item) {
            @Override
            public void convert(ViewHolder holder, PoiInfo item, int position) {
                holder.setText(R.id.item_tv_name, item.getName());
                holder.setText(R.id.item_tv_address, item.getAddress());
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                String message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(PoiSearchActivity.this, "请输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPoiSearch != null) {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(message)
                            .keyword(message)
                            .pageNum(loadIndex)
                            .cityLimit(false)
                            .scope(1));
                }

                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    /***-----Poi----***/
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(PoiSearchActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        List<PoiInfo> tempList = result.getAllPoi();
        if (tempList != null) {
            mList.addAll(tempList);
            adapter.notifyDataSetChanged();
        } else {

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
