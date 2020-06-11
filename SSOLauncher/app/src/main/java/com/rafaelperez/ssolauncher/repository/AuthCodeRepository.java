package com.rafaelperez.ssolauncher.repository;

import com.rafaelperez.ssolauncher.domain.ValidateAuthCodeRequest;
import com.rafaelperez.ssolauncher.domain.AuthCodeResponse;
import com.rafaelperez.ssolauncher.network.AuthCodeClient;
import com.rafaelperez.ssolauncher.network.AuthCodeWebService;

import retrofit2.Call;

public class AuthCodeRepository {
    private AuthCodeWebService webService;

    public AuthCodeRepository() {
        webService = AuthCodeClient.getClient().create(AuthCodeWebService.class);
    }

    public Call<AuthCodeResponse> validateAuthCode(ValidateAuthCodeRequest request) {
        return webService.validateAuthCode(request);
    }
}
