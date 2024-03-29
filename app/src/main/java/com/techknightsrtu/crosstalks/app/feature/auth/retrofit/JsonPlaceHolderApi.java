package com.techknightsrtu.crosstalks.app.feature.auth.retrofit;

import com.techknightsrtu.crosstalks.app.feature.auth.models.CaptchaResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    @FormUrlEncoded
    @POST("/recaptcha/api/siteverify")
    Call<CaptchaResponse> getCaptchaResponse(
        @Field("secret") String secret,
        @Field("response") String responseToken
    );
}
