package com.ieeevit.eventoadmin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ieeevit.eventoadmin.NetworkAPIs.FetchAPI;
import com.ieeevit.eventoadmin.NetworkModels.QRResponseModel;
import com.ieeevit.eventoadmin.R;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRActivity extends AppCompatActivity {

    String session_obj_id;
    SurfaceView cameraPreview;
    TextView txtresult;
    TextView title, desc;
    Button scan_again;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    boolean session_scan;
    final int RequestCameraPermissionId = 1001;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(QRActivity.this, HomeActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionId: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivirty);

        title = findViewById(R.id.qr_title);
        desc = findViewById(R.id.qr_desc);

        String titleStr = getIntent().getExtras().getString("title");
        String descStr = getIntent().getExtras().getString("desc");

        title.setText(titleStr);
        desc.setText(descStr);

        scan_again = findViewById(R.id.scan_again_btn);

        session_scan = getIntent().getExtras().getBoolean("session");

        session_obj_id = getIntent().getExtras().getString("session_id");

        cameraPreview = findViewById(R.id.cameraPreview);
        txtresult = findViewById(R.id.textresult);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build();

        scan_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.stop();
                try {
                    if (ActivityCompat.checkSelfPermission(QRActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(QRActivity.this, new String[]{
                                Manifest.permission.CAMERA}, 101);
                        return;
                    }
                    txtresult.setText("");
                    cameraSource.start(cameraPreview.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRActivity.this, new String[]{
                            Manifest.permission.CAMERA}, 101);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0) {
                    txtresult.post(new Runnable() {
                        @Override
                        public void run() {

                            cameraSource.stop();

                            String qr_code = qrcodes.valueAt(0).displayValue;
                            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ieeeevento.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
                            FetchAPI model = retrofit.create(FetchAPI.class);

                            String token = getSharedPreferences("AdminDetails", Context.MODE_PRIVATE).getString("token", "");

                            Call<QRResponseModel> modelCall = null;

                            if (session_scan)
                                modelCall = model.qrResponse(token, session_obj_id, qr_code);
                            else
                                modelCall = model.allocateWifiCoupon(token, qr_code);

                            //txtresult.setText(qrcodes.valueAt(0).displayValue);

                            final ProgressDialog progressDialog = new ProgressDialog(QRActivity.this);
                            progressDialog.setMessage("Scanning...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            modelCall.enqueue(new Callback<QRResponseModel>() {
                                @Override
                                public void onResponse(Call<QRResponseModel> call, Response<QRResponseModel> response) {
                                    cameraSource.stop();
                                    progressDialog.dismiss();
                                    if (response.body() == null) {
                                        txtresult.setText("Scan Again");
                                        return;
                                    }
                                    txtresult.setText(response.body().getMessage());

                                }
                                @Override
                                public void onFailure(Call<QRResponseModel> call, Throwable t) {
                                    progressDialog.dismiss();
                                    txtresult.setText("Scan Again");
                                    cameraSource.stop();
                                    scan_again.performClick();
                                }
                            });
                        }
                    });
                }
            }
        });


    }
}