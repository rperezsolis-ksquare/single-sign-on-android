package com.rafaelperez.ssolauncher.network;

import com.rafaelperez.ssolauncher.domain.AppItem;

public class AppItemNetworkProvider {

    public AppItem[] fetchAppItems() {
        //todo: fetch real apps info from network
        AppItem[] mockedArray = new AppItem[2];
        mockedArray[0] = new AppItem(
                "app 1",
                "https://3.bp.blogspot.com/-d-RdcVoyCxw/W_1FOqLOyEI/AAAAAAAAAPg/FDBb4t2UMf0tPQ6xMhxlzKRpf-UYMe24QCLcBGAs/s1600/ic_launcher.png",
                "com.rafaelperez.singlesignon1");
        mockedArray[1] = new AppItem(
                "Third party app",
                "https://3.bp.blogspot.com/-d-RdcVoyCxw/W_1FOqLOyEI/AAAAAAAAAPg/FDBb4t2UMf0tPQ6xMhxlzKRpf-UYMe24QCLcBGAs/s1600/ic_launcher.png",
                "com.rafaelperez.thirdpartyapp");
        return mockedArray;
    }
}
