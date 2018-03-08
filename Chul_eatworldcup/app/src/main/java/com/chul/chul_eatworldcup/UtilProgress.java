package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by leeyc on 2018. 3. 8..
 */

public class UtilProgress extends Activity implements DialogInterface.OnCancelListener {

    private Context mContext;
    private String mLogMarker = "";
    private Dialog mDialog = null;
    private boolean mIsCancel = false;


    public UtilProgress(Context context) {
        super();
        mContext = context;
        //mLogMarker = String.valueOf((int) (Math.random() * 1000));
    }


    public void addProgress() {
        addProgress(this);
    }

    public void addProgress(DialogInterface.OnCancelListener listener) {
        mDialog = ProgressDialogUtil.show(mContext, mDialog);
        mIsCancel = false;

        if (listener != null)
            mDialog.setOnCancelListener(listener);
        else
            mDialog.setOnCancelListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        //ToastUtil.toastShort(mContext, "사용자에 의해 취소 되었습니다.");
        mIsCancel = true;
        this.cancel();
    }


    public void cancel() {
        ProgressDialogUtil.dismiss(mDialog);
    }
}
