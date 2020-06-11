package com.rafaelperez.ssolauncher.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.rafaelperez.ssolauncher.R;
import com.rafaelperez.ssolauncher.databinding.FragmentLoggedBindingImpl;
import com.rafaelperez.ssolauncher.domain.AppItem;
import com.rafaelperez.ssolauncher.viewmodel.LoggedViewModel;
import com.rafaelperez.ssolauncher.viewmodel.LoggedViewModelFactory;

public class LoggedFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoggedBindingImpl binding = DataBindingUtil.inflate(inflater, R.layout.fragment_logged, container, false);
        LoggedFragmentArgs args = LoggedFragmentArgs.fromBundle(getArguments());
        String accessToken = args.getAccessToken();
        LoggedViewModelFactory viewModelFactory = new LoggedViewModelFactory(accessToken);
        LoggedViewModel viewModel = new ViewModelProvider(getActivity(), viewModelFactory).get(LoggedViewModel.class);
        recyclerView = binding.appsRecyclerView;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        viewModel.getAppItems().observe(getViewLifecycleOwner(), new Observer<AppItem[]>() {
            @Override
            public void onChanged(AppItem[] appItems) {
                if (appItems.length>0) {
                    adapter = new LoggedFragmentAdapter(appItems, getContext(), accessToken);
                    recyclerView.setAdapter(adapter);
                } else {
                    Snackbar.make(getView(), "There are no apps available", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }
}