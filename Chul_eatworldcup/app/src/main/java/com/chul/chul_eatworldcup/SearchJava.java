package com.chul.chul_eatworldcup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.chul.chul_eatworldcup.MainActivity.dong;
import static com.chul.chul_eatworldcup.SplashActivity.lati;
import static com.chul.chul_eatworldcup.SplashActivity.longti;

/**
 * Created by leeyc on 2017. 12. 20..
 */

public class SearchJava extends NMapActivity{
    private static final String CLIENT_ID = "wLIIkD1v3F7aYIpTjdXF";//"BDfRJ_qbTvaVbD3QdC6Y";
    private static final String clientSecret = "CIMd9Lu_vl";//"4fwk2LFzPr";

    String foodname;
    ///String dong="test";
    ArrayList<restaurantList>resList = new ArrayList<>();

    ///NMAP
    private NMapView mMapView;
    NMapLocationManager mMapLocationManager;
    NMapController mMapController;
    NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapCompassManager mMapCompassManager;
    private SharedPreferences mPreferences;

    private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(longti, lati);
    private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
    private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
    private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
    private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

    private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
    private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
    private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
    private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
    private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
    private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

    private static final boolean DEBUG = false;

    boolean testChk = false;
    private NMapPOIitem mFloatingPOIitem;
    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;
   //// private MapContainerView mMapContainerView;

    NGeoPoint nGeoPoint = new NGeoPoint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.search_test);
        Log.d("abcTest","selectedFoodActivity dong ="+dong);
        // create map view
        mMapView = new NMapView(this);

        // set Client ID for Open MapViewer Library
        mMapView.setClientId(CLIENT_ID);

        // initialize map view
        ///mMapView.setClickable(true);
        ///20180208_get dong without get map

        // set data provider listener
        super.setMapDataProviderListener(onDataProviderListener);
        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        ///mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();

        // create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.enableMyLocation(true);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        ////

        testChk =true;

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        int markerId = NMapPOIflagType.PIN;

        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);


        ///
        ////
        Intent getintent = getIntent();
        foodname= getintent.getStringExtra("foodName");

        Log.d("abcTest","foodname = "+foodname);
    }

       private boolean mIsMapEnlared = false;

    private void restoreInstanceState() {
        mPreferences = getPreferences(MODE_PRIVATE);

        int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
        int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
        boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
        boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

        mMapController.setMapViewMode(viewMode);
        ///mMapController.setMapViewTrafficMode(trafficMode);
        ///mMapController.setMapViewBicycleMode(bicycleMode);
        mMapController.setMapCenter(new NGeoPoint(longti, lati), level);

        if (mIsMapEnlared) {
            mMapView.setScalingFactor(2.0F);
        } else {
            mMapView.setScalingFactor(1.0F);
        }
    }



    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {


            Log.d("abcTest", "onReverseGeocoderResponse: placeMark="
                    + ((placeMark != null) ? placeMark.toString() : null));
            dong = (placeMark != null) ? placeMark.toString() : null;

            if(dong!=null&& !(dong.equals("test"))){
                gofind();
            }

            if (DEBUG) {
                Log.d("abcTest", "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e("bcdTest", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(SearchJava.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
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

                restoreInstanceState();
                findPlacemarkAtLocation(longti, lati);
                Log.d("abcTest","after findPlaceMark");

            } else { // fail
                Log.e("abcTest", "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(SearchJava.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
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

            ///nGeoPoint= mMapLocationManager.getMyLocation();
            if(testChk) {
                ///findPlacemarkAtLocation(nGeoPoint.getLongitude(), nGeoPoint.getLatitude());
                testChk = false;
            }
            if (mMapController != null) {
                mMapController.animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            Runnable runnable = new Runnable() {
                public void run() {
                    stopMyLocation();
                }
            };
            runnable.run();

            Toast.makeText(SearchJava.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(SearchJava.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }
    };
    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

               /// mMapContainerView.requestLayout();
            }
        }
    }


    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {

        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

    };


    /**
     * Container view class to rotate map view.
     */
    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mMapView.isAutoRotateEnabled()) {
                        int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }

                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void gofind(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("abcA1","test");

                    resList = getXmlData();

                Intent intent = new Intent(SearchJava.this,restaurantResult.class);
                intent.putExtra("resList",resList);


                //// need to check
                //mMapView.setOnMapStateChangeListener(null);
                setMapDataProviderListener(null);

                mMapLocationManager.enableMyLocation(false);
                mMapLocationManager.setOnLocationChangeListener(null);

                stopMyLocation();

                ////

                startActivity(intent);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("abcA2","test");
                    }
                });
            }
        }).start();
    }


    public ArrayList<restaurantList> getXmlData(){

        Log.d("getXmlData","getXmlData()");

           restaurantList rList  =new restaurantList();
           ArrayList<restaurantList>fList = new ArrayList<>();
          // Log.d("abcA3","test");
            String str= dong+foodname; //get foodName for str
           //String str= et1.getText().toString(); //EditText에 작성된 Text얻어오기
            String location = URLEncoder.encode(str); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..
          // Log.d("abcA6","test");

           String queryUrl= "https://openapi.naver.com/v1/search/local.xml?" +
                   "query="+location+"&display=10&start=1&sort=random";

           Log.d("abcA7","test");

            try {
                Log.d("abcAt1","test");

                URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
                Log.d("abcAt2","test");

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setRequestMethod("GET");
                Log.d("abcB1_3","test");
                connection.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
                Log.d("abcB1_4","test");
                connection.setRequestProperty("X-Naver-Client-Secret", clientSecret);//발급받은PW
                Log.d("abcB1_5","test");

                int responseCode = connection.getResponseCode();

               Log.d("abcAt3","test");

                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                Log.d("abcAt4","test");
                XmlPullParser xpp= factory.newPullParser();
                Log.d("abcAt5","test");


                if(responseCode==200)
                {
                    xpp.setInput( new InputStreamReader(connection.getInputStream()) );  //inputstream 으로부터 xml 입력받기
                }else {
                    xpp.setInput( new InputStreamReader(connection.getErrorStream()));
                }
                Log.d("abcAt6","test");

                String tag;

                xpp.next();

                int eventType= xpp.getEventType();

                while( eventType != XmlPullParser.END_DOCUMENT ){

                    switch( eventType ){

                        case XmlPullParser.START_DOCUMENT:

                            Log.d("abcde","start = "+"start NAVER XML parsing...\n\n");

                            break;

                        case XmlPullParser.START_TAG:

                            tag= xpp.getName();    //테그 이름 얻어오기

                            if(tag.equals("item")) ;// 첫번째 검색결과

                            else if(tag.equals("title")){

                                xpp.next();

                                if(xpp.getText().contains("Naver Open API"))
                                {

                                }
                                else{

                                    fList.add(rList);

                                    rList=new restaurantList();

                                    rList.setResNM(xpp.getText());

                                }
                            }

                            else if(tag.equals("category")){

                                xpp.next();

                                Log.d("abcTest","cate tag = "+tag+"& "+xpp.getText());

                                rList.setResCategory(xpp.getText());
                                Log.d("abcTest","rListCa"+rList.getResCategory());

                            }

                            else if(tag.equals("description")){

                                xpp.next();
                                Log.d("abcTest","tagA = "+tag);

                            }

                            else if(tag.equals("telephone")){

                                xpp.next();

                                Log.d("abcTest","tele tag = "+tag+"& "+xpp.getText());

                                rList.setResPhonNum(xpp.getText());

                            }

                            else if(tag.equals("address")){
                                xpp.next();

                                Log.d("abcTest","address tag = "+tag+"& "+xpp.getText());

                                rList.setResAddr(xpp.getText());
                                Log.d("abcTest","rListAddr"+rList.getResAddr());

                            }

                            else if(tag.equals("mapx")){
                                xpp.next();

                                Log.d("abcTest","MAPX tag = "+tag+"& "+xpp.getText());
                                rList.setResMapX(xpp.getText());
                            }

                            else if(tag.equals("mapy")){
                                xpp.next();

                                Log.d("abcTest","MAPY tag = "+tag+"& "+xpp.getText());
                                rList.setResMapY(xpp.getText());
                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:

                            break;
                    }

                    eventType= xpp.next();
                    if(eventType==XmlPullParser.END_DOCUMENT){
                        Log.d("abcTest","END_DOCU");
                        fList.add(rList);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("abcE1","test");
            }
            Log.d("abcTest","fList, size = "+fList.size());
            return fList;
        }//getXmlData method....

}
