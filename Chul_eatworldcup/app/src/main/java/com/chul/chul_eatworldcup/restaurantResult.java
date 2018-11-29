package com.chul.chul_eatworldcup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.util.ArrayList;
import java.util.Collections;

import static com.chul.chul_eatworldcup.MainActivity.dong;
import static com.chul.chul_eatworldcup.MainActivity.lati2;
import static com.chul.chul_eatworldcup.MainActivity.longti2;
import static com.chul.chul_eatworldcup.MainActivity.si;

/**
 * Created by leeyc on 2018. 1. 12..
 */

public class restaurantResult extends NMapActivity{
    private static final String CLIENT_ID = "wLIIkD1v3F7aYIpTjdXF";//"BDfRJ_qbTvaVbD3QdC6Y";
    TextView rtv1;

    ///map
    LinearLayout MapContainer;

    ///NMAP
    private NMapView mMapView;
    NMapLocationManager mMapLocationManager;
    NMapController mMapController;
    NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapCompassManager mMapCompassManager;
    private SharedPreferences mPreferences;

    private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
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

    private static boolean DEBUG = false;

    private NMapPOIitem mFloatingPOIitem;
    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;

    private MapContainerView mMapContainerView;

    NGeoPoint nGeoPoint = new NGeoPoint();
    AlertDialog.Builder mdialog;
    AlertDialog ad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurantresult);
        mdialog = new AlertDialog.Builder(restaurantResult.this);
        ad = mdialog.create();
        // create map view
        mMapView = new NMapView(this);

        // set Client ID for Open MapViewer Library
        mMapView.setClientId(CLIENT_ID);

        Log.d("abcTest","rest.java onCreate");

        /// get previous data

        ArrayList<restaurantList>restList;

        Intent gintent = getIntent();

        restList = (ArrayList<restaurantList>)gintent.getSerializableExtra("resList");

        Log.d("abcTest","restList size = "+restList.size());

        if(restList.size()<2){

            Log.d("abcTest","resta in if");

            searchExpand();
        }else{
            Log.d("abcTest","resta in else");

            restList.remove(0);

            Collections.shuffle(restList);

            rtv1 = (TextView)findViewById(R.id.rtv1);
            Log.d("abcTest",restList.get(0).getResNM()+"\n"+restList.get(0).getResCategory()+"\n"+restList.get(0).getResPhonNum()+"\n"+restList.get(0).getResAddr()+"\n"+restList.get(0).getResMapX()+"\n"+restList.get(0).getResMapY());
            //rtv1.setText(restList.get(0).getResNM()+"\n"+restList.get(0).getResCategory()+"\n"+restList.get(0).getResPhonNum()+"\n"+restList.get(0).getResAddr()+"\n"+restList.get(0).getResMapX()+"\n"+restList.get(0).getResMapY());
            rtv1.setText(restList.get(0).getResNM());
            // finish();

            Double lati = restList.get(0).getResMapX();
            Double longti = restList.get(0).getResMapY();

            Log.d("abcTest","rest R lati = "+lati);
            Log.d("abcTest","rest R longti = "+longti);

            MapContainer = (LinearLayout)this.findViewById(R.id.MapContainer);

            // 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다.
            MapContainer.addView(mMapView);
            // initialize map view
            mMapView.setClickable(true);

            // set data provider listener
            super.setMapDataProviderListener(onDataProviderListener);

            // register listener for map state changes
            mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
            mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

            // use map controller to zoom in/out, pan and set map center, zoom level etc.
            mMapController = mMapView.getMapController();

            // create resource provider
            mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

            // location manager
            mMapLocationManager = new NMapLocationManager(this);
            mMapLocationManager.enableMyLocation(false);
            mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

            // create overlay manager
            mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

//            // register callout overlay listener to customize it.
//            mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
//            // register callout overlay view listener to customize it.
//            mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

            // compass manager
            mMapCompassManager = new NMapCompassManager(this);

            // create my location overlay
            mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

            int markerId = NMapPOIflagType.PIN;

    // set POI data
            Log.d("abcTest","setPOI data");
            NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
            poiData.beginPOIdata(1);
            nGeoPoint.latitude=longti;
            nGeoPoint.longitude=lati;
            poiData.addPOIitem(nGeoPoint,restList.get(0).getResNM(),markerId,0);
            //poiData.addPOIitem(127.1687969,37.4421439,"test",markerId,1);
            poiData.endPOIdata();


    // create POI data overlay
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

            // show all POI data
            poiDataOverlay.showAllPOIdata(1);

            // set event listener to the overlayr
            poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

           // DEBUG=true;

            // register callout overlay listener to customize it.
            mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        }

    }

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Log.d("abcTest","countOfOverlappedItems >1 in restResult");
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                Log.d("abcTest","onCalloutOverlayViewListener. onCreateCalloutOverlayView");
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(restaurantResult.this, itemOverlay, overlayItem, itemBounds);
                }
            }

            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };



    void searchExpand(){
        stopMyLocation();

        Log.d("abcTest","searchExpand() in resResultacti");

        mdialog.setTitle("주변에 선택한 음식점이 없습니다..");

        mdialog.setPositiveButton("토너먼트 다시하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent00 = new Intent(restaurantResult.this,MainActivity.class);
                intent00.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent00);
            }
        });
        mdialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("abcTest","onDestroy in resResult");
        if (mdialog != null) {
            ad.dismiss();
            mdialog = null;
        }

        //종료
        mMapLocationManager.removeOnLocationChangeListener(onMyLocationChangeListener);
        finish();
    }


    /* POI data State Change Listener*/
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
                Log.d("abcTest", "onCalloutClick: title=" + item.getTitle());

            // [[TEMP]] handle a click event of the callout
            Toast.makeText(restaurantResult.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
                if (item != null) {
                    Log.d("lee", "onFocusChanged: " + item.toString());
                } else {
                    Log.d("abcTest", "onFocusChanged: ");
                }
        }
    };



    private boolean mIsMapEnlared = false;

    private void restoreInstanceState() {
        mPreferences = getPreferences(MODE_PRIVATE);
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
        boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
        boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

        mMapController.setMapViewMode(viewMode);
        //mMapController.setMapCenter(new NGeoPoint(longti2, lati2), level);
        mMapController.setMapCenter(nGeoPoint,level);
        Log.d("abcTest","new Map center Test");
        //mMapController.setMapCenter(new NGeoPoint(127.0630205,37.5091300), level);
        if (mIsMapEnlared) {
            mMapView.setScalingFactor(2.0F);
        } else {
            mMapView.setScalingFactor(1.0F);
        }
    }



    /* NMapDataProvider Listener */
    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            if (DEBUG) {
                Log.d("bcdTest", "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e("bcdTest", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(restaurantResult.this, errInfo.toString(), Toast.LENGTH_LONG).show();
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
                Log.d("abcTest","onMapViewStateChangeListener in resResult");
                restoreInstanceState();

            } else { // fail
                Log.e("abcTest", "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(restaurantResult.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.d("abcTest", "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.d("abcTest", "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.d("abcTest", "onZoomLevelChange: level=" + level);
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

            Log.d("abcTest","onMyLocationChangeListener in resResult");

            ///nGeoPoint= mMapLocationManager.getMyLocation();

//            if (mMapController != null) {
//                mMapController.animateTo(myLocation);
//            }

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

            Toast.makeText(restaurantResult.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(restaurantResult.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }
    };
    private void stopMyLocation() {
        Log.d("abcTest","stopMyLocation in resResult");
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d("abcTest","onBackpressed");
        mdialog.setTitle("카테고리로 돌아가시겠습니까?");

        mdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(restaurantResult.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


        mdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        mdialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lee","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lee","onRestart");
    }
}