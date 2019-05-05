package com.ieeevit.eventoadmin.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.ieeevit.eventoadmin.Activities.Fragments.SessionFragment;
import com.ieeevit.eventoadmin.Activities.Fragments.MealsFragment;
import com.ieeevit.eventoadmin.Activities.Fragments.OtherFragment;
import com.ieeevit.eventoadmin.Activities.Fragments.SwagsFragment;
import com.ieeevit.eventoadmin.Classes.Session;
import com.ieeevit.eventoadmin.NetworkAPIs.FetchAPI;
import com.ieeevit.eventoadmin.NetworkModels.EventSessionsModel;
import com.ieeevit.eventoadmin.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    private List<Session> meals;
    private List<Session> others;
    private List<Session> sessions;
    private List<Session> swags;
    private List<Session> events;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    ProgressDialog progressDialog;


    private final String USER_BASE_URL_AUTH = "https://ieeeevento.herokuapp.com/";

    private final String USER_BASE_URL = "https://ieeeevento.herokuapp.com/";
    SharedPreferences sharedPreferences;

    private static final int REQUEST_CODE_QR_SCAN = 101;

    private  final String LOGTAG="SCAN UR QR";

    FloatingActionButton wifi_allocate;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        wifi_allocate = findViewById(R.id.wifi_allocation_btn);
        wifi_allocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, QRActivity.class);
                i.putExtra("session_obj_id", "");
                i.putExtra("session", false);
                i.putExtra("title", "Wifi coupon allocation");
                i.putExtra("desc", "");
                startActivity(i);
            }
        });

        meals = new ArrayList<>();
        others = new ArrayList<>();
        sessions = new ArrayList<>();
        swags = new ArrayList<>();
        events = new ArrayList<>();


        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Loading the event sessions...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin App");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(USER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        FetchAPI fetchAPI = retrofit.create(FetchAPI.class);


        sharedPreferences = getSharedPreferences("AdminDetails", Context.MODE_PRIVATE);

        String eventId = sharedPreferences.getString("EventID", "");

        Call<EventSessionsModel> eventSessionsModelCall = fetchAPI.eventSessions(eventId);

        eventSessionsModelCall.enqueue(new Callback<EventSessionsModel>() {
            @Override
            public void onResponse(Call<EventSessionsModel> call, Response<EventSessionsModel> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                if (success.equals("false")) {
                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Session> responseSessions = response.body().getSessions();
                for (Session session : responseSessions) {
                    switch (session.getScannableType()) {
                        case "Meal":
                            meals.add(session);
                            break;
                        case "Session":
                            sessions.add(session);
                            break;
                        case "Swag":
                            swags.add(session);
                            break;
                        case "Others":
                            others.add(session);
                            break;
                        case "Event Registration":
                            events.add(session);
                            break;
                    }
                    setupViewPager(viewPager);
                }
            }

            @Override
            public void onFailure(Call<EventSessionsModel> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Network Error! Please reload", Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.logout)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", "");
            editor.putString("EventID", "");
            editor.commit();
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);

        }
        else if(item.getItemId()==R.id.add_a_scannable){
            Intent i=new Intent(this,AddScannableActivity.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.refresh) {


            meals = new ArrayList<>();
            others = new ArrayList<>();
            sessions = new ArrayList<>();
            swags = new ArrayList<>();
            events = new ArrayList<>();

            final ProgressDialog progressDialog1 = new ProgressDialog(HomeActivity.this);
            progressDialog1.setMessage("Loading the event sessions...");
            progressDialog1.setCancelable(false);
            progressDialog1.show();

            String eventId = sharedPreferences.getString("EventID", "");

            Retrofit retrofit = new Retrofit.Builder().baseUrl(USER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            FetchAPI fetchAPI = retrofit.create(FetchAPI.class);

            Call<EventSessionsModel> eventSessionsModelCall = fetchAPI.eventSessions(eventId);

            eventSessionsModelCall.enqueue(new Callback<EventSessionsModel>() {
                @Override
                public void onResponse(Call<EventSessionsModel> call, Response<EventSessionsModel> response) {
                    progressDialog1.dismiss();
                    String success = response.body().getSuccess().toString();
                    String message = response.body().getMessage();
                    if (success.equals("false")) {
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Session> responseSessions = response.body().getSessions();
                    for (Session session : responseSessions) {
                        switch (session.getScannableType()) {
                            case "Meal":
                                meals.add(session);
                                break;
                            case "Session":
                                sessions.add(session);
                                break;
                            case "Swag":
                                swags.add(session);
                                break;
                            case "Others":
                                others.add(session);
                                break;
                            case "Event Registration":
                                events.add(session);
                                break;
                        }
                        setupViewPager(viewPager);
                    }
                }

                @Override
                public void onFailure(Call<EventSessionsModel> call, Throwable t) {
                    t.printStackTrace();
                    progressDialog1.dismiss();
                    Toast.makeText(HomeActivity.this, "Network Error! Please Refresh from top right menu", Toast.LENGTH_LONG).show();
                }
            });

        }
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SessionFragment(sessions), "Sessions");
        adapter.addFrag(new MealsFragment(meals), "Meals");
        adapter.addFrag(new SwagsFragment(swags), "Swags");
        adapter.addFrag(new OtherFragment(others), "Others");
        adapter.addFrag(new OtherFragment(events), "Event Registration");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            Toast.makeText(HomeActivity.this,result,Toast.LENGTH_LONG).show();
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }

}


