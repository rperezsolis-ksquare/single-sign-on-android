package com.rafaelperez.thirdpartyapp.network;

import com.rafaelperez.thirdpartyapp.domain.AuthCodeRequest;
import com.rafaelperez.thirdpartyapp.domain.AuthCodeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthCodeWebService {

    @POST("authCode/getAuthCode")
    Call<AuthCodeResponse> getAuthCode(@Body AuthCodeRequest request);
}
