package com.chul.chul_eatworldcup;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;


public class ProgressDialogUtil {

    public static Dialog show(Context context, Dialog dialog) {
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
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
                return dialog;
            }

            dialog = new Dialog(context);

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
