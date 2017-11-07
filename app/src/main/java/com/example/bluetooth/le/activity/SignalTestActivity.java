package com.example.bluetooth.le.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.receiver.BluetoothReceiver;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexClientAPI;
import com.hexing.bluetooth.db.model.LocationStore;
import com.hexing.bluetooth.thread.WriteTask;
import com.hexing.bluetooth.util.CyclicCT;

import org.litepal.LitePal;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SignalTestActivity extends Activity implements View.OnClickListener {
    public LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;        //是否是首次定位
    public int b;
    public double bb;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView mDataField;
    public Object mData;
    private BluetoothReceiver bluetoothReceiver;
    public double a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());     //初始化
        setContentView(R.layout.activity_signal_test);


        Button stopct = (Button) findViewById(R.id.button_stop_ct);//停止发送CT命令
        stopct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commond = "7E0017880153544F504354";
                Log.e("待发送蓝牙完整指令:", commond);
                try {
                    WriteTask writeTaskct = new WriteTask();
                    writeTaskct.execute(commond);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(false);        //开启定位图层
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(SignalTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(SignalTestActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(SignalTestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(SignalTestActivity.this, permissions, 1);
        } else {
            //requestLocation();
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mDataField = (TextView) findViewById(R.id.data_value);
        Button button_ct = (Button) findViewById(R.id.button_ct);
        mDataField = (TextView) findViewById(R.id.data_value);
        //发送CT命令
        button_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_ct:
                        String commond = "7E0004080143545F";
                        Log.e("待发送蓝牙完整指令:", commond);
                        break;
                    default:
                        break;
                }
            }
        });


        baiduMap.setOnMapClickListener(new OnMapClickListener() {
            // 设置marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.logo_marker);

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            //点击地图监听
            @Override
            public void onMapClick(LatLng latLng) {
                //获取经纬度
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                System.out.println("latitude=" + latitude + ",longitude=" + longitude);
                LatLng point = new LatLng(latitude, longitude);
                MarkerOptions options = new MarkerOptions().position(point)
                        .icon(bitmap)
                        .draggable(true);// 设置手势拖拽
                if (isFirstLocate) {
                    isFirstLocate = false;
                    baiduMap.addOverlay(options);
                }
                //实例化一个地理编码查询对象
                GeoCoder geoCoder = GeoCoder.newInstance();
                //设置反地理编码位置坐标
                //ReverseGeoCodeOption op = new ReverseGeoCodeOption();
                // op.location(latLng);
                //发起反地理编码请求(经纬度->地址信息)
                //geoCoder.reverseGeoCode(op);
                OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                    // 反地理编码查询结果回调函数
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        if (result == null
                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            // 没有检测到结果
                            Toast.makeText(SignalTestActivity.this, "抱歉，未能找到结果",
                                    Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(SignalTestActivity.this,
                                "位置：" + result.getAddress(), Toast.LENGTH_LONG)
                                .show();
                    }

                    // 地理编码查询结果回调函数
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                        if (result == null
                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            // 没有检测到结果
                            Toast.makeText(SignalTestActivity.this, "抱歉，未能找到结果",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                // 设置地理编码检索监听者
                geoCoder.setOnGetGeoCodeResultListener(listener);
                //
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }
        });
    }


    protected BluetoothImpl callback = new BluetoothImpl() {
        @Override
        public void receiver(Object data) {
            super.receiver(data);
            LoadingDialog.cancelLoadingDialog();
            mData = data;
            makePoint(data);
            callbackResult(data);
            Log.e("data", "data");
        }
    };

    /**
     * 解析 返回数据
     *
     * @param data 原数据
     */
    private void callbackResult(Object data) {
        if (data.toString().length() < 0) {
            Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_LONG).show();
        } else {
            mDataField.setText(data.toString() + "dBm");
        }
    }


    @Override
    public void onClick(View v) {

    }


    public void makePoint(Object data) {
        if (data != null) {
            requestLocation();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Track_item:
                break;
        }

        return super.onOptionsItemSelected(item);
    }







    /*
     * @param location
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void navigateTo(final BDLocation location) {
        b = Integer.parseInt((String) mData);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        // 定位信息 传输进去 比例尺传输进去
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,
                Float.parseFloat("18"));
        baiduMap.animateMapStatus(u);
        location.getRadius(); //获取定位精度
        if (b != 0) {
            if (b >= (-70)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.green1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date date = new Date(System.currentTimeMillis());// new Date()为获取当前系统时间
                String cdate = df.format(date);
                LocationStore locationstore = new LocationStore();
                locationstore.setDate(cdate);
                locationstore.setLatitude(location.getLatitude());
                locationstore.setLongitude(location.getLongitude());
                locationstore.setSignal(b);
                locationstore.save();
                b = 0;

            } else if (b >= (-90) & b <= (-71)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.blue1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date date = new Date(System.currentTimeMillis());// new Date()为获取当前系统时间
                String cdate = df.format(date);
                LocationStore locationstore = new LocationStore();
                locationstore.setDate(cdate);
                locationstore.setLatitude(location.getLatitude());
                locationstore.setLongitude(location.getLongitude());
                locationstore.setSignal(b);
                locationstore.save();
                b = 0;

            } else if (b >= (-110) & b <= (-91)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.yello1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date date = new Date(System.currentTimeMillis());// new Date()为获取当前系统时间
                String cdate = df.format(date);
                LocationStore locationstore = new LocationStore();
                locationstore.setDate(cdate);
                locationstore.setLatitude(location.getLatitude());
                locationstore.setLongitude(location.getLongitude());
                locationstore.setSignal(b);
                locationstore.save();
                b = 0;
            } else if (b >= (-120) & b <= (-111)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.red1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date date = new Date(System.currentTimeMillis());// new Date()为获取当前系统时间
                String cdate = df.format(date);
                LocationStore locationstore = new LocationStore();
                locationstore.setDate(cdate);
                locationstore.setLatitude(location.getLatitude());
                locationstore.setLongitude(location.getLongitude());
                locationstore.setSignal(b);
                locationstore.save();
                b = 0;
            } else if (b <= (-121)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.purple1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date date = new Date(System.currentTimeMillis());// new Date()为获取当前系统时间
                String cdate = df.format(date);
                LocationStore locationstore = new LocationStore();
                locationstore.setDate(cdate);
                locationstore.setLatitude(location.getLatitude());
                locationstore.setLongitude(location.getLongitude());
                locationstore.setSignal(b);
                locationstore.save();
                b = 0;
            }
        }


        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            BDLocation finall = location;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        Toast.makeText(SignalTestActivity.this, "nav to" + finall.getAddrStr(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        });

    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option;   //获取各种丰富的地址信息
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedAddress(true);      //获取当前位置详细信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        HexClientAPI.getInstance().addListener(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SignalTest Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class MyLocationListener implements BDLocationListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            } else if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {

                navigateTo(location);
            }
        }
    }
}
