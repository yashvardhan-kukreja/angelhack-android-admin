package com.ieeevit.eventoadmin.NetworkAPIs;

import com.ieeevit.eventoadmin.NetworkModels.EventModel;
import com.ieeevit.eventoadmin.NetworkModels.EventSessionsModel;
import com.ieeevit.eventoadmin.NetworkModels.QRResponseModel;
import com.ieeevit.eventoadmin.NetworkModels.UserModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FetchAPI {

    @GET("user/fetch/personal-info")
    Call<UserModel> userInfo(
            @Header("x-access-token") String token
    );

    @POST("event/fetch/info")
    @FormUrlEncoded
    Call<EventModel> eventInfo(
            @Field("event_id") String event_id
    );

    @POST("/event/fetch/scannables")
    @FormUrlEncoded
    Call<EventSessionsModel> eventSessions(
      @Field("event_id") String event_id
    );

    @POST("/coordinator/mark-attendance")
    @FormUrlEncoded
    Call<QRResponseModel> qrResponse(
            @Header("x-access-token") String token,
            @Field("scannable_session_obj_id") String scannable_obbj_id,
            @Field("qr_code") String qr_code
    );


    @POST("/coordinator/allocate-wifi-coupon")
    @FormUrlEncoded
    Call<QRResponseModel> allocateWifiCoupon(
            @Header("x-access-token") String token,
            @Field("qr_code") String qr_code
    );
}
