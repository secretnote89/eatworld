package com.chul.chul_eatworldcup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by leeyc on 2017. 12. 27..
 */

public class SplashActivity extends Activity {


    public static final String PREFS_NAME = "MyPrefsFile";
    public static boolean isFirstRun=true;

    private GetContacts mTask;
    String TestSpalsh = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME,0);
        Log.d("abcTest","Splash & onCreate");

        /// 20180306 gps auth test
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Log.d("abcTest","in no permission");

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Log.d("abcTest","in no & reqPermiRationale");
            }
            Log.d("abcTest","in no P & before reqP");
            //get auth
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
            Log.d("abcTest","in no P & after reqP");
        }
        else{
            //already get auth
            Log.d("abcTest","already get auth");

            //startService(new Intent(SplashActivity.this, GpsService.class));
            goNext();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ///get auth
                    Log.d("abcTest","onReqPermiResult case 1 if");
                    startService(new Intent(SplashActivity.this, GpsService.class));
                    goNext();
                }else{
                    // denied auth
                    Log.d("abcTest","onReqPermiResult case 1 else");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                        dialog.setTitle("위치권한이 없으면 실행할 수 없습니다.");
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("abcTest","위치권한 선택에서 확인 선택!");
                                Intent intentback = new Intent(SplashActivity.this,SplashActivity.class);
                                startActivity(intentback);
                            }
                        });
                        dialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("abcTest","위치권한 선택에서 종료를 선택!");
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                        dialog.show();

                }
                return ;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d("abcTest","onBackPressed()");

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
        dialog.setTitle("종료하시겠습니까?");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);

                finish();

                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent0 = new Intent(SplashActivity.this, SplashActivity.class);
                startActivity(intent0);
            }
        });
        dialog.show();

    }

    public void goNext(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("abcTest","thread");

                    mTask = new GetContacts();
                    mTask.execute("abc");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("abcTest","thread runOnUi");
                    }
                });
            }
        }).start();

    }

    private class GetContacts extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            Log.d("abcTest","here??");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String...params)
        {

            Log.d("abcTest","there??");
            TestSpalsh = "after";
            //Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_SHORT).show();

            SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME,0);
            isFirstRun = mPrefs.getBoolean("isFirstRun",true);

            Log.d("abcTest", "SP "+isFirstRun);

            checkFirstRun();

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.d("abcTest","real??");
            super.onPostExecute(result);


            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);

            finish();
        }

}

    public void checkFirstRun()
    {
        Log.d("abcTest","CheciFirstRun");

        if(isFirstRun)
        {
            SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = mPrefs.edit();
            Log.d("abcTest","firstRun");
            editor.putBoolean("isFirstRun",false).apply();

            final DBHelper dbHelper = new DBHelper(getApplicationContext(),"DBHelper.db",null,1);
            SQLiteDatabase mDatabase;

            mDatabase = dbHelper.getWritableDatabase();

            FoodListDetail foodListDetail = new FoodListDetail();


            mDatabase.beginTransaction();

            try {
                Log.d("abcTest", "Try "+ foodListDetail.kor.length);
                //한식
                for(int i=0; i<foodListDetail.kor.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "K1", foodListDetail.kor[i], 0);
                    Log.d("abcTest", "Try "+ foodListDetail.kor[i]);
                }

                //일식
                for(int i=0; i<foodListDetail.jap.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "J1", foodListDetail.jap[i], 0);

                }//FOOD_LIST_KOR
                    //중식
                for(int i=0; i<foodListDetail.chi.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "C1", foodListDetail.chi[i], 0);
                }
                    //아시아
                for(int i=0; i<foodListDetail.asia.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "A1", foodListDetail.asia[i], 0);
                }
                    //양식
                for(int i=0; i<foodListDetail.eng.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "E1", foodListDetail.eng[i], 0);
                }
                    //분식
                for(int i=0; i<foodListDetail.dduk.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "D1", foodListDetail.dduk[i], 0);
                }
                    //치킨피자
                for(int i=0; i<foodListDetail.chicken.length;i++) {
                    dbHelper.onInsert("FOOD_LIST", "CH1", foodListDetail.chicken[i], 0);
                }
                    //패스트푸드
//                for(int i=0; i<foodListDetail.ham.length;i++)
//                    dbHelper.onInsert("FOOD_LIST","HM1",foodListDetail.ham[i],0);

                mDatabase.setTransactionSuccessful();

            }catch (Exception e){
                Log.d("abcTest", "Catch ");
                e.printStackTrace();
            }finally {
                mDatabase.endTransaction();
                mDatabase.close();
            }

        }
    }
}




