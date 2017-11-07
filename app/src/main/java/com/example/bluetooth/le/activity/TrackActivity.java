package com.example.bluetooth.le.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.bluetooth.le.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hexing.bluetooth.db.model.LocationStore;

import org.litepal.crud.DataSupport;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.location.d.j.i;
import static com.baidu.location.d.j.k;
import static com.example.bluetooth.le.R.id.track1;
import static com.example.bluetooth.le.R.id.track2;
import static com.example.bluetooth.le.R.id.track3;
import static com.example.bluetooth.le.R.id.track4;
import static com.example.bluetooth.le.R.id.track5;
import static com.example.bluetooth.le.R.id.track6;
import static com.example.bluetooth.le.R.layout.main;
import static com.example.bluetooth.le.activity.SignalTestActivity.*;
import static org.litepal.crud.DataSupport.where;

public class TrackActivity extends Activity {
    public LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private GoogleApiClient client;
    private double bb;
    public int j = 0;
    public boolean i = true;
    public int k = 0;
    public double[][] trackpoint;
    public int size;
    public double Latitude;
    public double Longitude;
    public double Signal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());     //初始化
        setContentView(R.layout.activity_track);
        mLocationClient = new LocationClient(getApplicationContext());
        //mLocationClient.registerLocationListener(new MyLocationListener());
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(false);        //开启定位图层
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(TrackActivity.this, permissions, 1);
        } else {
            //requestLocation();
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Intent intent = getIntent();
        String date1 = intent.getStringExtra("extra_data1");
        String date2 = intent.getStringExtra("extra_data2");
        final List<LocationStore> locationstores = DataSupport.where("date>=?", date1).where("date<=?", date2)
                .select("latitude", "longitude", "signal").find(LocationStore.class);
        size = locationstores.size();
        trackpoint = new double[size][3];

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                {

                        for (LocationStore locationstore : locationstores) {
                            Latitude = locationstore.getLatitude();
                            Longitude = locationstore.getLongitude();
                            Signal = locationstore.getSignal();
                            if (j < size) {
                                trackpoint[j][0] = Latitude;
                                trackpoint[j][1] = Longitude;
                                trackpoint[j][2] = Signal;
                                synchronized (this){
                                    navigatetrackTo(trackpoint[j]);
                                }
                                j++;
                            }
                        }


                }
                Looper.loop();
            }
        }, 2000);
    }

    public void navigatetrackTo(double[] location) {

        LatLng ll = new LatLng(location[0], location[1]);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(19f);
        baiduMap.animateMapStatus(update);
        bb = location[2];
        if (bb != 0) {
            if (bb >= (-70)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.green1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                bb = 0;

            } else if (bb >= (-90) & bb <= (-71)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.blue1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                bb = 0;

            } else if (bb >= (-110) & bb <= (-91)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.yello1);//此处设置自己的图标即可
                //构建MarkerOption,用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                bb = 0;

            } else if (bb >= (-120) & bb <= (-111)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.red1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                bb = 0;

            } else if (bb <= (-121)) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.purple1);//此处设置自己的图标即可
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap);
                baiduMap.addOverlay(option);    //在地图上添加Marker，并显示
                bb = 0;

            }
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Track Page") // TODO: Define a title for the content shown.
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
}

