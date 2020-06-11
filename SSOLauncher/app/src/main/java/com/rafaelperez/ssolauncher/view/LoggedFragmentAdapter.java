package com.rafaelperez.ssolauncher.view;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.rafaelperez.ssolauncher.R;
import com.rafaelperez.ssolauncher.domain.AppItem;

public class LoggedFragmentAdapter extends RecyclerView.Adapter<LoggedFragmentAdapter.AppItemViewHolder> {

    private AppItem[] appItems;
    private Context context;
    private String accessToken;

    public LoggedFragmentAdapter(AppItem[] appItems, Context context, String accessToken) {
        this.appItems = appItems;
        this.context = context;
        this.accessToken = accessToken;
    }

    @Override
    public AppItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent, false);
        return new AppItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(AppItemViewHolder holder, int position) {
        AppItem appItem = appItems[position];
        holder.bind(appItem, accessToken);
    }

    @Override
    public int getItemCount() {
        return appItems.length;
    }

    public static class AppItemViewHolder extends RecyclerView.ViewHolder {
        private static final String KEY_AUTHTOKEN = AccountManager.KEY_AUTHTOKEN;
        public ImageView imageView;
        public TextView textView;
        private Context context;
        private View view;

        public AppItemViewHolder(View view, Context context) {
            super(view);
            imageView = view.findViewById(R.id.appIcon);
            textView = view.findViewById(R.id.appName);
            this.context = context;
            this.view = view;
        }

        public void bind(AppItem appItem, String accessToken) {
            //todo: change static icons for appItem.getIconUrl() when fetch real apps info
            //Glide.with(context).load(appItem.getIconUrl()).into(imageView);
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            textView.setText(appItem.getName());
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appItem.getPackageName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(launchIntent);
                    } catch (Exception exception) {
                        Snackbar.make(view, "The app is not available", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}