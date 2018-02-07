package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leeyc on 2018. 1. 4..
 */

public class selectedFoodActivity extends Activity {

    ///if image selected remove other one
    ImageView tournimgv1;
    ImageView tournimgv2;
    TextView tourtv1;
    TextView tourtv2;
    LinearLayout layout;
    Button btn_back;
    Button btn_find;
    TextView tv_vs;
    Button v_invisible;



    int cur;
    ArrayList<String> SelectedFoodList = new ArrayList<String>();
    ArrayList<String>gridFoodName_sef = new ArrayList<String>();

    GradientDrawable gradientdrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tourament);

        tournimgv1= (ImageView)this.findViewById(R.id.lilayiv1);
        tournimgv2= (ImageView)this.findViewById(R.id.lilayiv2);
        tourtv1= (TextView)this.findViewById(R.id.lilaytv1);
        tourtv2= (TextView)this.findViewById(R.id.lilaytv2);
        layout = (LinearLayout)this.findViewById(R.id.layout_lower);
        btn_back = (Button)this.findViewById(R.id.btn_back);
        btn_find = (Button)this.findViewById(R.id.btn_find);
        tv_vs = (TextView)this.findViewById(R.id.tv_vs);
        v_invisible = (Button) this.findViewById(R.id.v_invisible);

        gradientdrawable = new GradientDrawable();

        gradientdrawable.setShape(GradientDrawable.RECTANGLE);

        gradientdrawable.setColor(Color.BLUE);
        gradientdrawable.setColor(Color.GRAY);

        gradientdrawable.setStroke(3, Color.BLACK);

        gradientdrawable.setCornerRadius(25.0f);



        ///receive data

        Intent intent_sef = getIntent();
        gridFoodName_sef=intent_sef.getStringArrayListExtra("gridFoodName"); ///shuffled foodname
        final int number = intent_sef.getIntExtra("num",0);

        if (number == 1){
            // Go To Final Food Selected

            //view final food photo and show btn1,2 // 1 = get recommand rest
            // 2= go back another game

            showCustomToast("우승!!",500);

            tournimgv1.setImageResource(getResources().getIdentifier("@drawable/"+gridFoodName_sef.get(0),"drawable",this.getPackageName()));
            tourtv1.setText(getResources().getIdentifier(gridFoodName_sef.get(0),"string",this.getPackageName()));

            tournimgv2.setVisibility(View.GONE);
            tourtv2.setVisibility(View.GONE);

            tv_vs.setVisibility(View.GONE);

            layout.setOrientation(LinearLayout.HORIZONTAL);


            btn_back.setVisibility(View.VISIBLE);
            v_invisible.setVisibility(View.INVISIBLE);
            btn_find.setVisibility(View.VISIBLE);

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(selectedFoodActivity.this,MainActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent2);
                }
            });

            btn_find.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
        else{

//            showCustomToast(" "+number+"강! ",500);
            tour_chk(number);



            cur = 0;

            setImages(gridFoodName_sef.get(cur*2), gridFoodName_sef.get(cur*2+1));

            tournimgv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        SelectedFoodList.add(gridFoodName_sef.get(cur * 2));
                        cur++;
                        if (cur == number / 2) {
                            // Call mySelf
                            Intent intent2 = new Intent(selectedFoodActivity.this, selectedFoodActivity.class);
                            intent2.putExtra("gridFoodName", SelectedFoodList);
                            intent2.putExtra("num", number / 2);
                            //intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent2);

                        } else {
                            setImages(gridFoodName_sef.get(cur * 2), gridFoodName_sef.get(cur * 2 + 1));
                        }
                }
            });
            tournimgv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        SelectedFoodList.add(gridFoodName_sef.get(cur*2+1));
                        cur++;
                        if (cur == number/2){
                            // Call mySelf
                            Intent intent2 = new Intent(selectedFoodActivity.this,selectedFoodActivity.class);
                            intent2.putExtra("gridFoodName",SelectedFoodList);
                            intent2.putExtra("num",number/2);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent2);
                        }
                        else{
                            setImages(gridFoodName_sef.get(cur*2), gridFoodName_sef.get(cur*2+1));
                        }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(selectedFoodActivity.this,MainActivity.class);
        startActivity(intent);

    }

    void setImages(String upFood, String downFood){
        tournimgv1.setImageResource(getResources().getIdentifier("@drawable/"+upFood,"drawable",this.getPackageName()));
        tournimgv2.setImageResource(getResources().getIdentifier("@drawable/"+downFood,"drawable",this.getPackageName()));
        tourtv1.setText(getResources().getIdentifier(upFood,"string",this.getPackageName()));
        tourtv2.setText(getResources().getIdentifier(downFood,"string",this.getPackageName()));
    }


    public void showCustomToast(String msg, int duration){

        //Retrieve the layout inflator
        LayoutInflater inflater = getLayoutInflater();
        //Assign the custom layout to view
        //Parameter 1 - Custom layout XML
        //Parameter 2 - Custom layout ID present in linearlayout tag of XML
        View layout = inflater.inflate(R.layout.result_toast,
                (ViewGroup) findViewById(R.id.result_toast));

        /*ImageView imageView = (ImageView)layout.findViewById(R.id.result_toast_iv1);
        imageView.setMaxHeight(500);
        imageView.setMaxHeight(300);
*/
        TextView msgView = (TextView)layout.findViewById(R.id.result_toast_tv1);
        msgView.setText(msg);
        msgView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        //Return the application context
        Toast toast = new Toast(getApplicationContext());
        //Set toast gravity to bottom
        toast.setGravity(Gravity.CENTER, 0, 0);
        //Set toast duration
        toast.setDuration(duration);
        //Set the custom layout to Toast
        toast.setView(layout);
        //Display toast
        toast.show();
    }


    public void tour_chk(int num){

        AlertDialog.Builder dialog = new AlertDialog.Builder(selectedFoodActivity.this);



        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                moveTaskToBack(true);
//
//                finish();
//
//                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.setNegativeButton("토너먼트 다시하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showCustomToast("토너먼트를 다시 시작합니다.",500);
                Intent intent2 = new Intent(selectedFoodActivity.this,MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent2);
                dialog.cancel();
            }
        });


        if(num==8)
        {
            showCustomToast("토너먼트 시작!",500);
        }else if(num==2){
            dialog.setTitle("결승!!");
            dialog.show();
        }else{
            dialog.setTitle(num+"강!");
            dialog.show();
        }


    }

}
