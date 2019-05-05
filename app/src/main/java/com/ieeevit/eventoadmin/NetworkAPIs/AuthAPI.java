package com.ieeevit.eventoadmin.NetworkAPIs;

import com.ieeevit.eventoadmin.NetworkModels.UserEventModel;
import com.ieeevit.eventoadmin.NetworkModels.UserLoginModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthAPI {

    @POST("authenticate/user/login")
    @FormUrlEncoded
    Call<UserLoginModel> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("user/verification")
    @FormUrlEncoded
    Call<UserEventModel> eventLogin(
            @Header("x-access-token") String token,
            @Field("event_id") String event_id
    );
}
