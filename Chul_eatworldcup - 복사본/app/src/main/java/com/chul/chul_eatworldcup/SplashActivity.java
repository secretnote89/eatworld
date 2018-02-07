package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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


    private int DELAY=0;
    private int PERIOD = 100;
    private Timer mTimer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTask = new GetContacts();
        mTask.execute("abc");

        Log.d("abcTest",TestSpalsh);
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




