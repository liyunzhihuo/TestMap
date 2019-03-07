package com.hssb.myclient.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.hssb.myclient.R;

public class SimpleMapActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /**
     * 当前地点击点
     */
    private LatLng currentPt;
    private String touchType;

    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_map_act);
        initUI();
    }


    private void initUI() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                touchType = "单击地图";
                currentPt = point;
                updateMapState();
            }

            @Override
            public boolean onMapPoiClick(MapPoi poi) {
//                touchType = "单击POI点";
//                currentPt = poi.getPosition();
//                updateMapState();
                return false;
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                updateMapState();
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                updateMapState();
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                updateMapState();
            }
        });
    }

    /**
     * 更新地图状态显示面板
     */
    private void updateMapState() {

        String state = "";
        if (currentPt == null) {
            state = "点击、长按、双击地图以获取经纬度和地图状态";
        } else {
            state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
                    currentPt.longitude, currentPt.latitude);
            MarkerOptions ooA = new MarkerOptions().position(currentPt).icon(bdA);
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooA);
        }
        state += "\n";
        MapStatus ms = mBaiduMap.getMapStatus();
        state += String.format(
                "zoom=%.1f rotate=%d overlook=%d",
                ms.zoom, (int) ms.rotate, (int) ms.overlook);
    }


    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();

    }
}
