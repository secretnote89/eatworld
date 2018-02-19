package com.chul.chul_eatworldcup;

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
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private LocationManager locationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        ///startLocationUpdates();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("abcTest","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("abcTest","onDestroy");
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
                Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intentGPS,1);
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
            return mBinder;
        }

        private final IBinder mBinder = new Binder() {

            @Override
            protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                Log.d("abcTest","onTransact");
                return super.onTransact(code, data, reply, flags);
            }
        };
    }