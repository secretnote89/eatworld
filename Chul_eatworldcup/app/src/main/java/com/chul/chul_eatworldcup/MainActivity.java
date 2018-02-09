package com.chul.chul_eatworldcup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends NMapActivity {
    private static final String CLIENT_ID = "wLIIkD1v3F7aYIpTjdXF";//"BDfRJ_qbTvaVbD3QdC6Y";
    private static final String clientSecret = "CIMd9Lu_vl";//"4fwk2LFzPr";

    private LocationManager locationManager;
    public static double lati;
    public static double longti;
    public static String dong="test";


    private NMapView mMapView;
    NMapLocationManager mMapLocationManager;
    NMapController mMapController;
    NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapCompassManager mMapCompassManager;
    private SharedPreferences mPreferences;

    private static final boolean DEBUG = false;

    private NMapPOIitem mFloatingPOIitem;
    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);

        setTitle("오늘 뭐먹지?");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d("abcTest","Main??");

        initMap();

        ///// get my location
        startLocationUpdates();

        /// origin
        final int img[] = {R.drawable.kor,R.drawable.jap,R.drawable.chi,R.drawable.asia,R.drawable.eng,R.drawable.dduk,R.drawable.chicken};
        ///remove img about ham
        String menuText[] = {"한식","일식","중식","아시아퓨전","양식","분식","치킨&피자&기타"};  ///remove and merge ham
        final String menuTextMatch[] = {"K1","J1","C1","A1","E1","D1","CH1"}; ///remove HM1

        final int setToggle[] = new int[img.length];

        for(int i=0;i<img.length;i++)
        setToggle[i]=1;                 // setToggle 값1로 초기화

        Log.d("abcTest","img");
        MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.row,img,menuText);


        GridView gv = (GridView)findViewById(R.id.gv1);
        gv.setAdapter(adapter);

        final TextView tv = (TextView)findViewById(R.id.tv1);

        Button goBtn = (Button)this.findViewById(R.id.btn_menu);

        goBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,TournamentActivity.class);
                intent.putExtra("Toggles",setToggle);
                intent.putExtra("TogMatch",menuTextMatch);
                intent.putExtra("lati",lati);
                intent.putExtra("longti",longti);
                startActivity(intent);
                                     }});

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                setToggle[position]=setToggle[position]*(-1);

                if(setToggle[position]>0)
                view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                else if(setToggle[position]<0)
                view.setBackgroundColor(getResources().getColor(R.color.colorLightSkyBlue));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }


    private void initMap(){

        // create map view
        mMapView = new NMapView(this);

        // set Client ID for Open MapViewer Library
        mMapView.setClientId(CLIENT_ID);

        // set data provider listener
        super.setMapDataProviderListener(onDataProviderListener);
        // register listener for map state changes
        ///mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
       /// mMapController = mMapView.getMapController();

        // create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.enableMyLocation(true);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        ////

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);
    }

    private void startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(MainActivity.this,"권한이 필요해애",Toast.LENGTH_SHORT).show();
            }

            //get auth
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            //already get auth

            Log.d("abcTest","already get auth");

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,10,mLocationListener);
        }
    }


    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            Log.d("abcTest", "onReverseGeocoderResponse: placeMark="
                    + ((placeMark != null) ? placeMark.toString() : null));
            dong = (placeMark != null) ? placeMark.toString() : null;

            if (errInfo != null) {
                Log.e("bcdTest", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(MainActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        }

    };

    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.

                Log.d("abcTest","in onMapViewStateCh~ onMapInitHandler ");
//                findPlacemarkAtLocation(longti, lati);
//                Log.d("abcTest","after findPlaceMark");

            } else { // fail
                Log.e("abcTest", "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(MainActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.d("bcdTest", "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.d("bcdTest", "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.d("bcdTest", "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }
    };

    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Log.d("abcTest","onMyLocationChangeListener");
            double lati2= mMapLocationManager.getMyLocation().getLatitude();
            double longti2= mMapLocationManager.getMyLocation().getLongitude();

            Log.d("abcTest","onMyLoc chan lati2 = "+lati2);
            Log.d("abcTest","onMyLoc chan longti2 = "+longti2);

            findPlacemarkAtLocation(longti2, lati2);
            Log.d("abcTest","onMyLoc chan find dong");
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

        }
    };

    /**
     * Container view class to rotate map view.
     */

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
//            lati = location.getLatitude();
//            longti = location.getLongitude();
//
//            Log.d("abcTest","msg = "+msg);
//            Log.d("abcTest","onLocationChange lati = "+lati);
//            Log.d("abcTest","onLocationChange longti = "+longti);
//
//            findPlacemarkAtLocation(longti, lati);
//            Log.d("abcTest","after findPlaceMark");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            Toast.makeText(getBaseContext(),"GPS is turn off!!!",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),"GPS is turn on!!",Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this,"권한이 없으면 안되는데...",Toast.LENGTH_SHORT).show();
                }
           return;
        }
    }

    @Override
    public void onBackPressed() {

        Log.d("abcTest","onBackpressed");

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
                dialog.cancel();
            }
        });
        dialog.show();

    }

}
class MyAdapter extends BaseAdapter{
    Context context;
    int layout;
    int img[];
    LayoutInflater inf;
    String[] menuText;

    public MyAdapter(Context context, int layout, int[] img, String[] menuText){
        this.context=context;
        this.layout=layout;
        this.img = img;
        this.menuText = menuText;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return img.length;
    }

    @Override
    public Object getItem(int position){
        return img[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView=inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
        iv.setImageResource(img[position]);

        TextView textView_grid = (TextView)convertView.findViewById(R.id.tv_grid1);
        textView_grid.setText(menuText[position]);
        textView_grid.setGravity(Gravity.BOTTOM);
        return convertView;

    }


}
