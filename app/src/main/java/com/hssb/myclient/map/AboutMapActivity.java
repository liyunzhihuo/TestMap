package com.hssb.myclient.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hssb.myclient.R;

public class AboutMapActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_map);
        mContext = AboutMapActivity.this;
    }

    public void simpleMap(View view) {
        startActivity(new Intent(mContext, SimpleMapActivity.class));
    }

    public void locationMap(View view) {
        startActivity(new Intent(mContext, LocationMapActivity.class));
    }
}
