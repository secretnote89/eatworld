package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);

        setTitle("오늘 뭐먹지?");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("abcTest","Main??");


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

//    @Override
//    protected void onDestroy() {
//        Log.d("abcTest","onDestroy()");
//        //super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
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
