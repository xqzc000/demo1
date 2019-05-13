package com.example.myapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    boolean isFromBackGround=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,MainActivity2.class),111);
            }
        });

    }


    /**
     *
     * 判断某activity是否处于栈顶
     * @return  true在栈顶 false不在栈顶
     */
    private boolean isActivityTop(Class cls, Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }



    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

//        isFromBackGround=!isRunningForeground();
        isFromBackGround=App.isBackGround();
        Log.i(TAG,this+"onStop="+isFromBackGround);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG,this+"onResume= begin"+isFromBackGround);
////        isFromBackGround=false;
////        Log.i(TAG,this+"onResume= after"+isFromBackGround);
        if(isFromBackGround){
            Toast.makeText(this,"验证",Toast.LENGTH_SHORT).show();
        }
        isFromBackGround=false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"requestCode="+requestCode+",resultCode="+resultCode+",data"+data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,this+"onStart=");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,this+"onRestart=");
    }
}
