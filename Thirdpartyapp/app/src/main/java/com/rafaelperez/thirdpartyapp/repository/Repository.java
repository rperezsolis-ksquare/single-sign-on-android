package com.rafaelperez.thirdpartyapp.repository;

import com.rafaelperez.thirdpartyapp.domain.AuthCodeRequest;
import com.rafaelperez.thirdpartyapp.domain.AuthCodeResponse;
import com.rafaelperez.thirdpartyapp.network.AuthCodeClient;
import com.rafaelperez.thirdpartyapp.network.AuthCodeWebService;

import retrofit2.Call;

public class Repository {
    private AuthCodeWebService webService;

    public Repository() {
        webService = AuthCodeClient.getClient().create(AuthCodeWebService.class);
    }

    public Call<AuthCodeResponse> getAuthCode(AuthCodeRequest request) {
        return webService.getAuthCode(request);
    }
}
