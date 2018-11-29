package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ProgressTest extends Activity{
    private View header;
    private ProgressBar mprogressbar;
    View vv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progresstest);

        Button bt22;
        bt22 = (Button)this.findViewById(R.id.bt22);

        //addContentView를 통해 추가 레이어를 올린다.
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vv  = vi.inflate(R.layout.progress, null);
        this.addContentView(vv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        bt22.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"on Click",Toast.LENGTH_SHORT).show();
                ((ViewGroup) vv.getParent()).removeView(vv);
            }
        });
    }
}
