package com.rafaelperez.ssolauncher.domain;

public class AppItem {
    private String name;
    private String iconUrl;
    private String packageName;

    public AppItem(String name, String iconUrl, String packageName) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getPackageName() {
        return packageName;
    }
}
