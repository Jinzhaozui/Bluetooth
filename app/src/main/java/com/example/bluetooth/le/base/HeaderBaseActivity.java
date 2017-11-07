package com.example.bluetooth.le.base;

import android.view.LayoutInflater;
import android.view.View;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.widget.HeaderLayout;


/**
 * 带有头部的baseActivity
 */
public class HeaderBaseActivity extends BaseActivity {

    protected HeaderLayout headerLayout;

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View topView = inflater.inflate(layoutResID, null);
        headerLayout = (HeaderLayout) topView.findViewById(R.id.headerLayout);
        super.setContentView(topView);
    }
}
