package com.rafaelperez.thirdpartyapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rafaelperez.thirdpartyapp.domain.AuthCodeRequest;
import com.rafaelperez.thirdpartyapp.domain.AuthCodeResponse;
import com.rafaelperez.thirdpartyapp.repository.Repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private Repository repository = new Repository();

    private MutableLiveData<AuthCodeResponse> authCode = new MutableLiveData<>();
    public LiveData<AuthCodeResponse> getAuthCode() {
        return authCode;
    }

    private MutableLiveData<String> error = new MutableLiveData<>();
    public LiveData<String> getError() {
        return error;
    }

    public void signIn() {
        getAuthCodeResponse();
    }

    public void getAuthCodeResponse() {
        AuthCodeRequest request = new AuthCodeRequest("third_party_app", "123456789");
        Call<AuthCodeResponse> call = repository.getAuthCode(request);
        call.enqueue(new Callback<AuthCodeResponse>() {
            @Override
            public void onResponse(Call<AuthCodeResponse> call, Response<AuthCodeResponse> response) {
                switch (response.code()){
                    case 200:
                        AuthCodeResponse authCodeResponse = response.body();
                        setAuthCodeValue(authCodeResponse);
                        break;
                    case 401:
                        error.setValue("Unauthorized client");
                        break;
                }
            }

            @Override
            public void onFailure(Call<AuthCodeResponse> call, Throwable t) {

            }
        });
    }

    private void setAuthCodeValue(AuthCodeResponse authCode) {
        this.authCode.setValue(authCode);
    }
}
