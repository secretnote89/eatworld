package com.chul.chul_eatworldcup;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class MainActivity extends NMapActivity {
    private static final String CLIENT_ID = "wLIIkD1v3F7aYIpTjdXF";//"BDfRJ_qbTvaVbD3QdC6Y";
    private static final String clientSecret = "CIMd9Lu_vl";//"4fwk2LFzPr";

    public static String dong="test";
    public static String si="test";
    public static double lati2;
    public static double longti2;

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

    private Messenger mServiceMessenger = null;
    private boolean mIsBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);


        setTitle("오늘 뭐먹지?");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //20180307 rev
        setStartService();


        Log.d("abcTest","Main??");

        initMap();

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


        ///////
        Button goBtn = (Button)this.findViewById(R.id.btn_menu);

        goBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                boolean checked = false;
                for(int i=0; i<setToggle.length;i++){
                    if(setToggle[i]==-1)
                    {
                        checked=true;
                        break;
                    }
                }


                if(checked)
                {
                    Log.d("abcTest","Main button Click checked");
                    Intent intent = new Intent(MainActivity.this,TournamentActivity.class);
                    intent.putExtra("Toggles",setToggle);
                    intent.putExtra("TogMatch",menuTextMatch);
                    //
                    startActivity(intent);
                }else{
                    Log.d("abcTest","Main button Click unchecked");
                    Toast.makeText(getApplicationContext(),"카테고리를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
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



    /** 서비스 시작 및 Messenger 전달 */
    private void setStartService() {
        Log.d("abcTest","setStartService");


        startService(new Intent(MainActivity.this, GpsService.class));
        Log.d("abcTest","startService");
        bindService(new Intent(MainActivity.this, GpsService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.d("abcTest","bindService");

        //20180307 rev
        sendMessageToService("from main");

        mIsBound = true;
    }

    /** 서비스 정지 */
    private void setStopService() {
        Log.d("abcTest","setStopService");
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        stopService(new Intent(MainActivity.this, GpsService.class));
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("abcTest","onServiceConnected");
            mServiceMessenger = new Messenger(iBinder);
            try {
                Message msg = Message.obtain(null, GpsService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
                Log.d("abcTest","exception = "+e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("abcTest","onService Disconnected");
        }
    };

    /** Service 로 부터 message를 받음 */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("abcTest","act : what "+msg.what);
            switch (msg.what) {
                case GpsService.MSG_SEND_TO_ACTIVITY:
                    int value1 = msg.getData().getInt("fromService");
                    //String value2 = msg.getData().getString("test");

                    if(value1==2){
                        Log.d("abcTest","in Main value1 = "+value1);
                        setStopService();
                        noticeGPSChk();
                    }else if(value1==3){
                        Log.d("abcTest","in Main value1 = "+value1);
                        noticeGPSChk();
                    }

                    break;
            }
            return false;
        }
    }));

    /** Service 로 메시지를 보냄 */
    private void sendMessageToService(String str) {
        Log.d("abcTest","str = "+str);
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, GpsService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
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
        ///mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.enableMyLocation(true);

        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        ////

        // create overlay manager
       /// mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // create my location overlay
       /// mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        // compass manager
        ///mMapCompassManager = new NMapCompassManager(this);

    }



    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            Log.d("abcTest", "onReverseGeocoderResponse: placeMark="
                    + ((placeMark != null) ? placeMark.toString() : null));
            dong = (placeMark != null) ? placeMark.toString() : "test";
            si = (placeMark!=null)? placeMark.siName.toString():"test";

            ///stop GPS & Naver Map
            //if(placeMark!=null)
            stopMyLocation();

            if (errInfo != null) {
                Log.e("abcTest", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

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


            Log.d("abcTest","onMyLocationChangeListener in Main");

            lati2= mMapLocationManager.getMyLocation().getLatitude();
            longti2= mMapLocationManager.getMyLocation().getLongitude();

            Log.d("abcTest","onMyLoc chan lati2 = "+lati2);
            Log.d("abcTest","onMyLoc chan longti2 = "+longti2);



            if(lati2!=0)
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

    private void stopMyLocation() {
        Log.d("abcTest","stopMyLocation");
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                /// mMapContainerView.requestLayout();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("abcTest","onResume in Main");
        super.setMapDataProviderListener(onDataProviderListener);
        setStartService();
        mMapLocationManager.enableMyLocation(true);
        //startService(new Intent(MainActivity.this, GpsService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("abcTest","onDestroy");
        setStopService();
        //stopService(new Intent(MainActivity.this, GpsService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("abcTest","onPause");
    }


    void noticeGPSChk(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        Log.d("abcTest","noticeGPSChk");
        dialog.setTitle("GPS가 OFF 상태일때는 이용할 수 없습니다.");
        dialog.setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intentGPS,2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("abcTest","onActivityResult");
        if (requestCode==2) {
            Log.d("abcTest","req code = "+requestCode);
            Intent intentback = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intentback);
        }
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
