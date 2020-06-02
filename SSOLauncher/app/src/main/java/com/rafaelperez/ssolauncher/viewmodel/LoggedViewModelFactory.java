package com.rafaelperez.ssolauncher.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoggedViewModelFactory implements ViewModelProvider.Factory {
    private String authToken;

    public LoggedViewModelFactory(String authToken) {
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoggedViewModel(authToken);
    }
}
