package com.example.bluetooth.le.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bluetooth.le.R;


public class MainActivity extends Activity {
    private TextView tv_meter;
    private TextView btn_find;
    private EditText track1;
    private EditText track2;
    private EditText track3;
    private EditText track4;
    private EditText track5;
    private EditText track6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_meter = (TextView) findViewById(R.id.tv_meter);
        // tv_lcu = (TextView) findViewById(R.id.tv_lcu);
        btn_find = (TextView) findViewById(R.id.btn_find);
        track1 = (EditText) findViewById(R.id.track1);
        track2 = (EditText) findViewById(R.id.track2);
        track3 = (EditText) findViewById(R.id.track3);
        track4 = (EditText) findViewById(R.id.track4);
        track5 = (EditText) findViewById(R.id.track5);
        track6 = (EditText) findViewById(R.id.track6);


        tv_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra("from", "METER");
                startActivity(intent);
            }
        });


        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String year1 = track1.getText().toString();
                String month1 = track2.getText().toString();
                String day1 = track3.getText().toString();
                String year2 = track4.getText().toString();
                String month2 = track5.getText().toString();
                String day2 = track6.getText().toString();
                String date11 = year1;
                String date12 = "-" + month1;
                String date13 = "-" + day1;
                String date1 = date11 + date12 + date13;
                String date21 = year2;
                String date22 = "-" + month2;
                String date23 = "-" + day2;
                String date2 = date21 + date22 + date23;
                Intent intent = new Intent(MainActivity.this, TrackActivity.class);
                intent.putExtra("extra_data1",date1);
                intent.putExtra("extra_data2",date2);
                startActivity(intent);
            }
        });

        /**
         * tv_lcu.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
        intent.putExtra("from", "LCU");
        startActivity(intent);
        }
        });

         tv_card.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
        intent.putExtra("from", "CARD");
        startActivity(intent);
        }
        });
         */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


}
