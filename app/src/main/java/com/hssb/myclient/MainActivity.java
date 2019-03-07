package com.hssb.myclient;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hssb.myclient.location.AboutLocationActivity;
import com.hssb.myclient.map.AboutMapActivity;
import com.hssb.myclient.map.PoiSearchActivity;
import com.hssb.myclient.map.SimpleMapActivity;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
    }

    public void aboutLocation(View view) {
        startActivity(new Intent(mContext, AboutLocationActivity.class));
    }

    public void aboutMap(View view) {
        startActivity(new Intent(mContext, AboutMapActivity.class));
    }

    public void aboutPoi(View view) {
        startActivity(new Intent(mContext, PoiSearchActivity.class));
    }
}
