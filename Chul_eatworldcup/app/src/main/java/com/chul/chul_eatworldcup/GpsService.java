package com.chul.chul_eatworldcup;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;

/**
 * Created by leeyc on 2018. 2. 16..
 */

public class GpsService extends Service {

    public static boolean GPSChk = false;
    public static boolean GPSOnOFFChk = false;

    public static final String PREFS_NAME = "MyPrefsFile";

    private LocationManager locationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    private Messenger mClient = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("abcTest","GpsService onCreate");


        ///startLocationUpdates();
        initializeLocationManager();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.d("abcTest", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d("abcTest", "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.d("abcTest", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d("abcTest", "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("abcTest","onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d("abcTest","onDestroy");
        super.onDestroy();
        if (locationManager != null) {
                try {
                    locationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.d("abcTest", "fail to remove location listners, ignore", ex);
                }
        }
    }

    private void initializeLocationManager() {
        Log.d("abcTest", "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
            ///locationManager.removeUpdates(mLocationListener);
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
            ///goGPS();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getBaseContext(),"GPS status change!!",Toast.LENGTH_SHORT).show();

            Log.d("abcTest","onStatusChang");
        }

    };

    void noticeGPSChk(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(GpsService.this);
        Log.d("abcTest","noticeGPSChk");
        dialog.setTitle("GPS가 OFF 상태일때는 이용할 수 없습니다.");
        dialog.setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("abcTest","disable, noticeGPSChk, setting");

                Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intentGPS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentGPS);
                //startActivityForResult(intentGPS,1);
            }
        });
        dialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ///moveTaskToBack(true);
                ///finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.show();
    }



        @Nullable
        @Override
        public IBinder onBind (Intent intent){
            Log.d("abcTest","onBind");
            return mMessenger.getBinder();
        }

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("abctest","ControlService - message what : "+msg.what +" , msg.obj "+ msg.obj);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;  // activity로부터 가져온
                    break;
            }
            return false;
        }
    }));

    private void sendMsgToActivity(int sendValue) {
        try {
            Bundle bundle = new Bundle();
            ////// send msg to act
            bundle.putInt("fromService", sendValue);
            bundle.putString("test","abcdefg");
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            mClient.send(msg);      // msg 보내기
        } catch (RemoteException e) {
        }
    }



}