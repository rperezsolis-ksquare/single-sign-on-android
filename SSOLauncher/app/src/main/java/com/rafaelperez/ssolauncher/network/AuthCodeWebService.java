package com.rafaelperez.ssolauncher.network;

import com.rafaelperez.ssolauncher.domain.ValidateAuthCodeRequest;
import com.rafaelperez.ssolauncher.domain.AuthCodeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthCodeWebService {

    @POST("authCode/validateAuthCode")
    Call<AuthCodeResponse> validateAuthCode(@Body ValidateAuthCodeRequest request);
}
