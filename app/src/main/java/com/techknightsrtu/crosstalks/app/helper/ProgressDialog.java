package com.techknightsrtu.crosstalks.app.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.techknightsrtu.crosstalks.R;

public class ProgressDialog {

    private Activity activity;
    private View progressDialogView;
    private AlertDialog progressDialog;

    public ProgressDialog(Activity activity){
        this.activity = activity;

        LayoutInflater inflater = LayoutInflater.from(activity);
        progressDialogView = inflater.inflate(R.layout.loading_dialog, null);
        progressDialog = new AlertDialog.Builder(activity).create();
        progressDialog.setView(progressDialogView);
    }

    public void showProgressDialog(){
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(240, 240);
    }

    public void hideProgressDialog(){
        progressDialog.hide();
    }

}
