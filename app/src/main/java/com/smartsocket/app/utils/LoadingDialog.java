package com.smartsocket.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.smartsocket.app.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity instance){
        this.activity = instance;
    }

    public void start(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_alert, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();

    }

    public void stop(){
        if(dialog != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }
}
