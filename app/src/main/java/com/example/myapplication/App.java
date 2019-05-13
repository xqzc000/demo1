package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class App extends Application {
    private static final String TAG = "Application";
    private static final String TAG2 = "Application2";
    static int count;
    private volatile boolean isDone;
    @NotNull
    public static App me;
    private float readSize=0;
    private int totalSize=0;



    public boolean isDone() {
        return isDone;
    }


    public String getReadRate() {
        float rate=0;
        if(totalSize!=0)
            rate=readSize/totalSize*100;
        return rate+"%";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        me=this;
        isDone= getSharedPreferences("db",MODE_PRIVATE).getBoolean("DONE",false);
        Log.i(TAG,"isDone="+isDone);
        if(!isDone)
            new Thread(new SaveDateRunnable()).start();

//        registerActivityLifecycleCallbacks();

    }

    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.i(TAG,"onActivityCreated=="+activity.toString());

            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i(TAG,"onActivityStarted=="+activity.toString());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                count++;
                Log.i(TAG,"onActivityResumed=="+activity.toString());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(TAG,"onActivityPaused=="+activity.toString());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                Log.i(TAG,"onActivityStopped=="+activity.toString());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i(TAG,"onActivitySaveInstanceState=="+activity.toString());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i(TAG,"onActivityDestroyed=="+activity.toString());
            }
        });
    }

    private boolean hasPermission() {
        boolean bool=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
        Log.i(TAG,"hasPermission="+bool);
        return bool;
    }

    public static boolean isBackGround(){
        return count<=0;
    }

    class SaveDateRunnable implements Runnable {
        @Override
        public void run() {
            String beginTxt="★☆☆   ";
            int start="★☆☆   ".length();
            String interval="————————————";
            int nums=0;
            boolean isWriting=false;//开始写数据库里
            BufferedReader bufferedReader=null;
            BufferedWriter writer=null;
             StringBuilder sb=null;
             String id=null;
            try {
                InputStream is  = getAssets().open("dic.txt");
                totalSize=is.available();
                Log.i(TAG,"filesize="+totalSize+",begin========");
                long time=System.currentTimeMillis();
                bufferedReader=new BufferedReader(new InputStreamReader(is));
//                writer=new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory(),"test.txt")));
                String text=null;
               while((text=bufferedReader.readLine())!=null){
                   readSize+=text.length();
                   if("".equals(text)){
                       continue;
                   }
                    if(interval.equals(text)){
                        if(isWriting) {
                            isWriting = false;
                            saveDB(id,sb.toString());
//                            writer.write(id+"=="+sb.toString());
//                            writer.flush();
//                            Log.i(TAG,id+"=="+sb.toString());
                            nums++;
                        }
                        continue;
                    }else {
                            if ((text.contains(beginTxt))&& isCharactor(text.substring(start))) {
                                isWriting=true;
                                id=text.substring(start);

                                sb=new StringBuilder();
                            }else if(!isWriting) {
                                  continue;
                            }else{
                                sb.append(text+"\n");
                            }

                    }
               }
               if(isWriting){ //防止最后一个单词未读取
                   saveDB(id,sb.toString());
                   nums++;
               }
                isDone=true;
                Log.i(TAG,"isDone"+isDone+",end=============="+ (System.currentTimeMillis()-time));
                getSharedPreferences("db",MODE_PRIVATE).edit().putBoolean("DONE",true).commit();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }finally {

                if(bufferedReader!=null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(writer!=null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            Log.i(TAG,"end nums="+nums);

        }
    }

    private void updateDB(String id, String content) {
        MyDatabaseOpenHelper.updateDB(id,content);
    }

    private void saveDB(String id, String content) {
        MyDatabaseOpenHelper.saveDB(id,content);
    }


    private boolean isCharactor(String str) {
        boolean bool=true;
        for(int i=0;i<str.length();i++){
            char ch=str.charAt(i);
            if((ch>='A' && ch<='Z')  ||  (ch>='a' && ch<='z')){

            }else{
                bool=false;
                break;
            }
        }
        return bool;
    }
}
