package com.chul.chul_eatworldcup;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


public class ProgressDialogUtil {

    public static Dialog show(Context context, Dialog dialog) {
        Log.d("abcTest","in ProgressDialogUtil show");
        return show(context, dialog, null, null);
    }

    public static Dialog show(Context context, Dialog dialog, OnDismissListener dismissListener) {
        return show(context, dialog, dismissListener, null);
    }

    public static Dialog show(Context context, Dialog dialog, OnCancelListener cancelListener) {
        return show(context, dialog, null, cancelListener);
    }

    public static Dialog show(final Context context, Dialog dialog, OnDismissListener dismissListener,
                              OnCancelListener cancelListener) {
        try {
            Log.d("abcTest","Dialog show try");
            if (dialog != null) {
                Log.d("abcTest","dialoug!=null");
                if (!dialog.isShowing()) {
                    Log.d("abcTest","!dialog.isShowing");
                    dialog.show();
                }
                return dialog;
            }

            dialog = new Dialog(context);
            Log.d("abcTest","new Dialog(context)");
/*

			LinearLayout layout = new LinearLayout(context);
			ProgressBar progressBar = new ProgressBar(context);

			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER);
			//layout.setBackgroundColor(0x00000000); // 백그라운드색
			layout.addView(progressBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));

			dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));*/
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View v = LayoutInflater.from(context).inflate(R.layout.progress, null);
            dialog.setContentView(v);

            dialog.setCancelable(false);

            if (cancelListener != null)
                dialog.setOnCancelListener(cancelListener);

            if (dismissListener != null)
                dialog.setOnDismissListener(dismissListener);

            dialog.show();

        } catch (Exception e) {

            //e.printStackTrace();

        }

        return dialog;
    }

    public static void dismiss(Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
