package com.dangerdasheng.android_frame.base;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.dangerdasheng.android_frame.R;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    //获取TAG的activity名称
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
    }

    @Override
    public void onClick(View v) {


    }
}
