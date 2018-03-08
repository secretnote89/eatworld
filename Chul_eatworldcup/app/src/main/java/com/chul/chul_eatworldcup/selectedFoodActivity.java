package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.chul.chul_eatworldcup.MainActivity.dong;
import static com.chul.chul_eatworldcup.MainActivity.lati2;
import static com.chul.chul_eatworldcup.MainActivity.longti2;

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
    LinearLayout layout2;
    Button btn_back;
    Button btn_find;
    TextView tv_vs;
    Button v_invisible;
    ProgressBar progressBar;
    ProgressBar loading_indicator;
    Handler mhandelr = new Handler();

    int cnt2=0;


    int cur;
    ArrayList<String> SelectedFoodList = new ArrayList<String>();
    ArrayList<String>gridFoodName_sef = new ArrayList<String>();

    GradientDrawable gradientdrawable;

    Timer timer = new Timer();
    boolean first;

    int count=0;

    //for searching
    TimerTask tt = new TimerTask() {
        @Override
        public void run() {
            count++;
            Log.d("abcTest","tt in selecActi count = "+count);
            goSearch();
        }
    };

    void notice(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(selectedFoodActivity.this);
            Log.d("abcTest","selectedFoodActi Timer tt");
            dialog.setTitle("죄송합니다.\n 재검색 하시겠습니까?");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goSearch();

                }
            });
            dialog.setNegativeButton("카테고리로 돌아가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    moveTaskToBack(true);
//                    finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
                    Intent intentback = new Intent(selectedFoodActivity.this,MainActivity.class);
                    startActivity(intentback);
                }
            });
            dialog.show();

    }

//    TimerTask tt2 = new TimerTask() {
//        @Override
//        public void run() {
//            timer.cancel();
//            AlertDialog.Builder dialog = new AlertDialog.Builder(selectedFoodActivity.this);
//            Log.d("abcTest","selectedFoodActi Timer tt");
//            dialog.setTitle("죄송합니다. 재검색 하시겠습니까?");
//            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intentback = new Intent(selectedFoodActivity.this,selectedFoodActivity.class);
//                    startActivity(intentback);
//                }
//            });
//            dialog.setNegativeButton("카테고리로 돌아가기", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
////                    moveTaskToBack(true);
////                    finish();
////                    android.os.Process.killProcess(android.os.Process.myPid());
//                    Intent intentback = new Intent(selectedFoodActivity.this,MainActivity.class);
//                    startActivity(intentback);
//                }
//            });
//            dialog.show();
//
//        }
//    };

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
        progressBar = (ProgressBar)this.findViewById(R.id.progBar);
        loading_indicator = (ProgressBar)this.findViewById(R.id.loading_indicator);

        //rev 20180308
        layout2 = (LinearLayout)this.findViewById(R.id.greatEGWi);
        first = true;

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

                    goSearch();

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
        public void goSearch(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("abcTest2","selectedFood Activity thread");


                ///Message message = handler.obtainMessage();
                ///handler.sendEmptyMessage(1);

                    //// progress로 바꾸기 예정
                    Log.d("abcTest","selected test lati2 = "+lati2);
                    Log.d("abcTest","selected test longti2 = "+longti2);
                    Log.d("abcTest","selected test dong = "+dong);
                    //rev 20180308
                if(dong.equals("test") || dong==null){

                    //dialog = new Dialog(mContext);
                    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Log.d("abcTest","setBackground Transparent in selectedFoodActivity");
                    mhandelr.post(new Runnable() {
                        @Override
                        public void run() {
                            //rev 20180308
                            Log.d("abcTest","progress if in selectedActi");
                            if(first){
                                Log.d("abcTest","first in selected Act");
                                first = false;
                                timer.schedule(tt,10,1*1000);
                                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress, null);
                                setContentView(v);
                            }

                            if(count>60){
                                timer.cancel();
                                count=0;
                                first=true;
                                notice();
                            }



                            cnt2+=10;
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.setProgress(cnt2);
//                            tournimgv1.setVisibility(View.GONE);
//                            tourtv1.setVisibility(View.GONE);
                            //loading_indicator.setProgress(cnt2);

                        }
                    });

                    //rev 20180308
                    Log.d("abcTest","selected test lati2 = "+lati2);
                    Log.d("abcTest","selected test longti2 = "+longti2);
                    Log.d("abcTest","selected test dong = "+dong);
                }else{
                    //dialog.dismiss();
                    timer.cancel();
                    Log.d("abcTest","selected else lati2 = "+lati2);
                    Log.d("abcTest","selected else longti2 = "+longti2);
                    Log.d("abcTest","selected else dong = "+dong);
                    Intent intent3 = new Intent(selectedFoodActivity.this,SearchJava.class);
                    intent3.putExtra("foodName",tourtv1.getText().toString());
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent3);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("abcTest","selectedFood Activity thread runOnUi");
                    }
                });
            }
        }).start();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        ///Thread.interrupted();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("abcTest","onDestroy in selectedFoodActi");
    }
}
