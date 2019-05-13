package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                setResult(9999);
                finish();
            }
        });

    }




    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,this+"onStop=");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,this+"onResume=");
    }
}
