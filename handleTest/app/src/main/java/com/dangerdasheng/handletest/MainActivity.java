package com.dangerdasheng.handletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    public static final int UPDATE = 0x1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
        begin();
    }

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case UPDATE:
//                    tv.setText(String.valueOf(msg.arg1));
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    private static class MyHandle extends Handler{
        WeakReference<MainActivity> weakReference;
        public MyHandle(MainActivity activity){
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = weakReference.get();
            if (activity!= null){
                activity.tv.setText(String.valueOf(msg.arg1));
            }

        }
    }
    private MyHandle myHandle = new MyHandle(this);

    public void begin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=5;i>0;i--){
                    Message msg = new Message();
                    msg.what = UPDATE;
                    msg.arg1 = i;
                    myHandle.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    Log.i("tag",MainActivity.this + "-" + i);
                }
                Intent intent = new Intent(MainActivity.this,Main3Activity.class);
                startActivity(intent);
                finish();
            }

        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandle.removeCallbacksAndMessages(null);
        Log.i("tag","destory");
    }
}
