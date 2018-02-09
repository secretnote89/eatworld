package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Random;

import java.util.ArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static com.chul.chul_eatworldcup.MainActivity.dong;
import static com.chul.chul_eatworldcup.MainActivity.lati;
import static com.chul.chul_eatworldcup.MainActivity.longti;

/**
 * Created by leeyc on 2017. 12. 30..
 */

public class TournamentActivity extends Activity {

    //match to layout

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.tourament);

        Intent intent = getIntent();
        int[] setToggle = intent.getIntArrayExtra("Toggles");

        String[] menuTextMatch = intent.getStringArrayExtra("TogMatch");    //get food code

        Log.d("abcTest","TournamentActivity lati ="+lati);
        Log.d("abcTest","TournamentActivity longti ="+longti);
        Log.d("abcTest","selectedFoodActivity dong ="+dong);

        ArrayList<String> menuMatchTour = new ArrayList<String>();
        ArrayList<Integer> menuNumber = new ArrayList<Integer>();

        Log.d("abcTest","Length="+menuTextMatch.length);

        int j=0;

        //food code 가져오기
        for (int i=0;i<setToggle.length;i++)
        {
            Log.d("abcTest","Value ["+i+"]="+setToggle[i]);
            switch (setToggle[i]){
                case -1 :
                    menuMatchTour.add(menuTextMatch[i]);        // get selected food cd
                    j++;                                        // get selected food num
                    break;

                default:
                    break;
            }
        }

        int menuEachNumber = 8/j; ///all devide by selected num
        for (int i=0; i<j; i++){
            menuNumber.add(menuEachNumber);
        }
        int menuRemainNumber = 8-menuEachNumber*j;
        for (int i=0; i<menuRemainNumber; i++){
            menuNumber.set(i, menuNumber.get(i)+1);
        }

        for (int i=0; i<j; i++) {
            Log.d("abcTest", "ABC "+i+": "+menuMatchTour.get(i)+", "+menuNumber.get(i));
        }

        /// 코드별로 음식명 정보들 가져와야됨

        ArrayList<ArrayList<FoodListC>> FoodList = new ArrayList<ArrayList<FoodListC>>();
        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"DBHelper.db",null,1);
        for (int i=0; i<j; i++) {
            FoodList.add(dbHelper.getList(menuMatchTour.get(i), menuNumber.get(i)));
        }

        final ArrayList<String> gridFoodName = new ArrayList<String>();

        for (int i=0; i<FoodList.size(); i++){
            for (int k=0; k<FoodList.get(i).size(); k++){
                gridFoodName.add(FoodList.get(i).get(k).getFood_NM());
            }
        }

        Collections.shuffle(gridFoodName);  ///shuffle

        Intent intent2 = new Intent(TournamentActivity.this,selectedFoodActivity.class);
        intent2.putExtra("gridFoodName",gridFoodName);
        intent2.putExtra("num",8);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

       // startActivityForResult(intent,1);
        startActivity(intent2);
        finish();
    }

}
