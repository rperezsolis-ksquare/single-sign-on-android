package com.rafaelperez.ssolauncher.repository;

import com.rafaelperez.ssolauncher.domain.AppItem;
import com.rafaelperez.ssolauncher.network.AppItemNetworkProvider;

public class AppItemRepository {
    private AppItemNetworkProvider appItemNetworkProvider;

    public AppItemRepository() {
        appItemNetworkProvider = new AppItemNetworkProvider();
    }

    public AppItem[] getAppItems() {
        return appItemNetworkProvider.fetchAppItems();
    }
}
