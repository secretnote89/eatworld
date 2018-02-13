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

    public static boolean GPSChk = false;
    public static boolean GPSOnOFFChk = false;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static boolean isFirstRun=true;


    private GetContacts mTask;
    String TestSpalsh = "";


    private int DELAY=0;
    private int PERIOD = 100;
    private Timer mTimer = new Timer();


    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME,0);
        GPSChk = mPrefs.getBoolean("GpsChk",false);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.d("abcTest","GPSChk = "+GPSChk);

        Log.d("abcTest","Splash & onCreate");
        startLocationUpdates();
        Log.d("abcTest","after startLoc~update()");
        GPSOnOFFChk = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

        if(GPSChk && GPSOnOFFChk){
            goGPS();
//            mTask = new GetContacts();
//            mTask.execute("abc");
        }else{
            Log.d("abcTest","GPS else");

            if(!GPSChk){
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                Log.d("abcTest","!GPSChk, GPSCHK = "+GPSChk+"& GPSOnOffChk = "+GPSOnOFFChk);
                dialog.setTitle("위치권한이 없으면 실행할 수 없습니다.");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentback = new Intent(SplashActivity.this,SplashActivity.class);
                        startActivity(intentback);
                    }
                });
                dialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                dialog.show();

            }else if(!GPSOnOFFChk){
                  Log.d("abcTest","else, GPSCHK = "+GPSChk+"& GPSOnOffChk = "+GPSOnOFFChk);

//                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
//                Log.d("abcTest","else, GPSCHK = "+GPSChk+"& GPSOnOffChk = "+GPSOnOFFChk);
//                dialog.setTitle("GPS가 OFF 상태일때는 이용할 수 없습니다.");
//                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        intentGPS.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(intentGPS);
//                        goGPS();
//                    }
//                });
//                dialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        moveTaskToBack(true);
//                        finish();
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                    }
//                });
//                dialog.show();
            }
        }
        Log.d("abcTest",TestSpalsh);
    }


    private void startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            GPSChk=false;

            Log.d("abcTest","in no permission");

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Log.d("abcTest","in no & reqPermiRationale");
            }
            Log.d("abcTest","in no P & before reqP");
            //get auth
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            Log.d("abcTest","in no P & after reqP");
        }
        else{
            //already get auth
            //GPSChk=true;
            Log.d("abcTest","already get auth");

            SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = mPrefs.edit();
            Log.d("abcTest","startLocationUpdates else sharedPref");
            editor.putBoolean("GpsChk",true).apply();
            GPSChk=true;
            //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,mLocationListener);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,10,mLocationListener);


            Log.d("abcTest","already after setting");
        }
    }

    /////////////
    private final LocationListener mLocationListener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {

            if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                Log.d("abcTest","where ?= GPS_PROVIDER");

            }else{
                Log.d("abcTest","where ?= NETWORK_PROVIDER");
            }
            String msg = "New Lati: "+location.getLatitude() + "New Longti: "+location.getLongitude();
            double lati;
            double longti;
            lati = location.getLatitude();
            longti = location.getLongitude();
            locationManager.removeUpdates(mLocationListener);
            Log.d("abcTest","msg = "+msg);
            Log.d("abcTest","onLocationChange lati = "+lati);
            Log.d("abcTest","onLocationChange longti = "+longti);
//
//            findPlacemarkAtLocation(longti, lati);
//            Log.d("abcTest","after findPlaceMark");
        }

        @Override
        public void onProviderDisabled(String provider) {

            noticeGPSChk();

            Log.d("abcTest","onProviderDisabled");
//            //Toast.makeText(getBaseContext(),"GPS is turn off!!!",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivityForResult(intent,1);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),"GPS is turn on!!",Toast.LENGTH_SHORT).show();
            Log.d("abcTest","onProviderEnabled");
            goGPS();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getBaseContext(),"GPS status change!!",Toast.LENGTH_SHORT).show();

            Log.d("abcTest","onStatusChang");
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //get auth ok?

                    Log.d("abcTest","onReqPermission code=1");
                    Intent intent = new Intent(SplashActivity.this,SplashActivity.class);
                    startActivity(intent);

                }else{
                    Log.d("abcTest","onReqPerSessionRez else");
                }
                return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("abcTest","onActivityResult");
        if (requestCode==1) {
            Log.d("abcTest","req code = "+requestCode);
                goGPS();

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

    public void goGPS(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("abcTest","thread");

                if(GPSChk && GPSOnOFFChk) {
                    Log.d("abcTest","in go GPS && all ok");
                    mTask = new GetContacts();
                    mTask.execute("abc");
                }else{
                    Intent intentback = new Intent(SplashActivity.this,SplashActivity.class);
                    startActivity(intentback);
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("abcTest","thread runOnUi");
                    }
                });
            }
        }).start();
    }
    void noticeGPSChk(){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                Log.d("abcTest","noticeGPSChk");
                dialog.setTitle("GPS가 OFF 상태일때는 이용할 수 없습니다.");
                dialog.setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intentGPS,1);
                    }
                });
                dialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                dialog.show();
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
            ///0.1초 딜레이
            // timerStart();
            //timerStop();
            /*
             Handler mHandler;
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                        //
                 }
             },100);
*/

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


    public void timerStart(){
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //
            }
        },DELAY,PERIOD);
    }

    public void timerStop()
    {
        mTimer.cancel();
    }

}




