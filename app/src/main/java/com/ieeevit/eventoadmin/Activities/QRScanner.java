
package com.ieeevit.eventoadmin.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.ieeevit.eventoadmin.NetworkAPIs.FetchAPI;
import com.ieeevit.eventoadmin.NetworkModels.QRResponseModel;
import com.ieeevit.eventoadmin.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    String emailLoggedIn;

    String session_obj_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        session_obj_id = getIntent().getExtras().getString("session_id").toString();

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        emailLoggedIn = getIntent().getExtras().getString("emailLoggedIn");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        scannerView.setAutoFocus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera(0);
    }

    @Override
    public void handleResult(Result result) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ieeeevento.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        FetchAPI model = retrofit.create(FetchAPI.class);

        String token = getSharedPreferences("AdminDetails", Context.MODE_PRIVATE).getString("token", "");

        Call<QRResponseModel> modelCall = model.qrResponse(token, session_obj_id, result.toString());
        Toast.makeText(QRScanner.this, result.toString(), Toast.LENGTH_LONG).show();
/*
        Intent i=new Intent(this,HomeActivity.class);
        startActivity(i);*/


        modelCall.enqueue(new Callback<QRResponseModel>() {
            @Override
            public void onResponse(Call<QRResponseModel> call, Response<QRResponseModel> response) {
                Toast.makeText(QRScanner.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<QRResponseModel> call, Throwable t) {
                Toast.makeText(QRScanner.this, "Scan Again", Toast.LENGTH_LONG).show();

            }
        });
    }

}

