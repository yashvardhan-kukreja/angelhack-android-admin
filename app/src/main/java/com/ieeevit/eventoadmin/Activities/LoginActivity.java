package com.ieeevit.eventoadmin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.ieeevit.eventoadmin.Classes.User;
import com.ieeevit.eventoadmin.NetworkAPIs.AuthAPI;
import com.ieeevit.eventoadmin.NetworkAPIs.FetchAPI;
import com.ieeevit.eventoadmin.NetworkModels.UserEventModel;
import com.ieeevit.eventoadmin.NetworkModels.UserLoginModel;
import com.ieeevit.eventoadmin.NetworkModels.UserModel;
import com.ieeevit.eventoadmin.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password, eventID;
    private Button loginBtn;
    private final String USER_BASE_URL_AUTH = "https://ieee-evento.herokuapp.com/";
    private TextView loadingText;
    private View loadingScreen;
    private LottieAnimationView loadingAnimation;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("AdminDetails", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_login);

        if(sharedPreferences.getString("token", "").equals("") || sharedPreferences.getString("token", "").equals(null)) {
        }
        else{
            Intent i=new Intent(this,HomeActivity.class);
            startActivity(i);
        }

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_pwd);
        eventID = findViewById(R.id.et_eventid);
        loginBtn = findViewById(R.id.btn_user_login);
        loadingScreen = findViewById(R.id.signup_loading_screen);
        loadingAnimation = findViewById(R.id.signup_loading_animation);
        loadingText = findViewById(R.id.signup_loading_text);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }

    private void userLogin(){
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter all the details", Toast.LENGTH_LONG).show();
        } else {
            View currentView = this.getCurrentFocus();
            if (currentView != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
            loadingScreen.setVisibility(View.VISIBLE);
            loadingAnimation.playAnimation();
            loadingText.setText("Authenticating");
            // Creating the retrofit instance
            Retrofit retrofit = new Retrofit.Builder().baseUrl(USER_BASE_URL_AUTH).addConverterFactory(GsonConverterFactory.create()).build();
            AuthAPI authAPI = retrofit.create(AuthAPI.class);

            // Network call for logging in
            Call<UserLoginModel> login = authAPI.userLogin(email.getText().toString(), password.getText().toString());
            login.enqueue(new Callback<UserLoginModel>() {
                @Override
                public void onResponse(Call<UserLoginModel> call, Response<UserLoginModel> response) {
                    String success = response.body().getSuccess().toString();
                    String message = response.body().getMessage();
                    loadingScreen.setVisibility(View.INVISIBLE);
                    loadingAnimation.pauseAnimation();
                    if (success.equals("false")){
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        String token = response.body().getToken();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();
                        getUser();
                    }
                }

                @Override
                public void onFailure(Call<UserLoginModel> call, Throwable t) {
                    loadingScreen.setVisibility(View.INVISIBLE);
                    loadingAnimation.pauseAnimation();
                    t.printStackTrace();
                    Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void getUser(){
        String token = sharedPreferences.getString("token", "");

        loadingScreen.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();
        loadingText.setText("Fetching User Details");

        // Creating the retrofit instance
        Retrofit retrofit = new Retrofit.Builder().baseUrl(USER_BASE_URL_AUTH).addConverterFactory(GsonConverterFactory.create()).build();
        FetchAPI fetchAPI = retrofit.create(FetchAPI.class);

        // Network call for logging in
        Call<UserModel> userInfo = fetchAPI.userInfo(token);
        userInfo.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                loadingScreen.setVisibility(View.INVISIBLE);
                loadingAnimation.pauseAnimation();
                if (success.equals("false")){
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    saveUserDetails(response);
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                loadingScreen.setVisibility(View.INVISIBLE);
                loadingAnimation.pauseAnimation();
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_LONG).show();
            }
        });

        // End of the network call
    }

    private void saveUserDetails(Response<UserModel> response){
        SharedPreferences sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        User currUser = response.body().getUser();
        Gson gson = new Gson();
        String json = gson.toJson(currUser);
        editor.putString("UserDetails", json);
        editor.commit();
        loginEvent();
    }

    private void loginEvent(){

        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
        loadingScreen.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();
        loadingText.setText("Finding Your Event");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(USER_BASE_URL_AUTH).addConverterFactory(GsonConverterFactory.create()).build();
        AuthAPI authAPI = retrofit.create(AuthAPI.class);
        // Network call for logging in
        final String eventText = eventID.getText().toString();
        final Call<UserEventModel> event = authAPI.eventLogin(sharedPreferences.getString("token", ""), eventText);
        event.enqueue(new Callback<UserEventModel>() {
            @Override
            public void onResponse(Call<UserEventModel> call, Response<UserEventModel> response) {
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                Boolean isCoordinator = response.body().getIsCoordinator();
                loadingScreen.setVisibility(View.INVISIBLE);
                loadingAnimation.pauseAnimation();
                if (success.equals("false")){
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    if (isCoordinator) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("EventID", eventText);
                        editor.apply();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "You are not Cordinator for this Event!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserEventModel> call, Throwable t) {
                loadingScreen.setVisibility(View.INVISIBLE);
                loadingAnimation.pauseAnimation();
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_LONG).show();
            }
        });
    }

}
