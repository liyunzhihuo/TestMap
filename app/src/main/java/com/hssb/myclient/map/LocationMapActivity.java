package com.hssb.myclient.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hssb.myclient.R;


public class LocationMapActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /**
     * 当前地点击点
     */
    private LatLng currentPt;
    private BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private boolean isUserClick = false;

    //定位
    private int mCurrentDirection = 0;
    boolean isFirstLoc = true; // 是否首次定位
    private LocationClient mLocationClient = null;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private MyLocationData locData;
    // 搜索模块
    //    113.006418,28.111567
    private GeoCoder mSearch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map_act);
        initMapView();
        initLocation();
        initGeoCoder();
    }

    private void initGeoCoder() {
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.e("Search", "onGetGeoCodeResult error 未能找到结果");
                    return;
                }
                Log.e("Search", "onGetGeoCodeResult");
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                    mBaiduMap.addOverlay(new MarkerOptions()
                            .position(result.getLocation())
                            .icon(bdA));
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
                }

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.e("Search", "onGetReverseGeoCodeResult error 未能找到结果");
                    return;
                }
                Log.e("Search", "onGetReverseGeoCodeResult");
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                    mBaiduMap.addOverlay(new MarkerOptions()
                            .position(result.getLocation())
                            .icon(bdA));
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
                }
            }
        });
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setOpenGps(true); // 打开gps
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        option.setOpenAutoNotifyMode();
        option.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        mLocationClient.setLocOption(option);
    }

    private void initMapView() {
        mMapView = findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        //为地图注册点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                isUserClick = true;
                currentPt = point;
                updateMapState();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //为地图注册状态变化事件
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
     * 添加覆盖物
     */
    private void updateMapState() {
        if (currentPt != null) {
//            state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
//                    currentPt.longitude, currentPt.latitude);
            MarkerOptions ooA = new MarkerOptions().position(currentPt).icon(bdA);
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooA);
            Log.e("updateMapState","currentPt.latitude ="+currentPt.latitude);
            setMapView(currentPt.longitude,currentPt.latitude);
        }
    }

    public void setDefault(View view) {
        setMapView(113.006418f, 28.111567);
    }

    private void setMapView(double lon, double lat) {
        LatLng ptCenter = new LatLng(lat, lon);
        int version = 0;
        if (mSearch != null) {
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter).newVersion(version).radius(500));
        }
    }

    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mBaiduMap != null) {
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mSearch != null) {
            mSearch.destroy();
        }
        super.onDestroy();
    }

    /**
     * 定位监听事件
     */
    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            Log.e("AboutLocationActivity", "latitude = " + latitude + " longitude = " + longitude
                    + "\n city = " + location.getCity() + " city code = " + location.getCityCode()
                    + "\n address = " + location.getAddress() + " 城市adcode = " + location.getAdCode()
                    + "\n 区/县信息 = " + location.getDistrict() + " 位置描述信息 = " + location.getLocationDescribe()
                    + "\n poi = " + location.getPoiList().toString());

            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            if (mBaiduMap != null && !isUserClick) {
                mBaiduMap.setMyLocationData(locData);
            }
            if (isFirstLoc && mBaiduMap != null) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
}
