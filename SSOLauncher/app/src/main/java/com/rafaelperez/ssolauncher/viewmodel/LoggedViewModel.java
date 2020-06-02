package com.rafaelperez.ssolauncher.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rafaelperez.ssolauncher.domain.AppItem;
import com.rafaelperez.ssolauncher.repository.AppItemRepository;

public class LoggedViewModel extends ViewModel {

    private AppItemRepository appItemRepository;
    private String authToken;

    private MutableLiveData<AppItem[]> appItems = new MutableLiveData<>();
    public LiveData<AppItem[]> getAppItems() {
        return appItems;
    }

    public LoggedViewModel(String authToken) {
        this.authToken= authToken;
        appItemRepository = new AppItemRepository();
        setAppItems();
    }

    public void setAppItems() {
        appItems.setValue(appItemRepository.getAppItems());
    }
}
