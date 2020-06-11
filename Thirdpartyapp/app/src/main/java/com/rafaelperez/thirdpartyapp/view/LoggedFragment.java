package com.rafaelperez.thirdpartyapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.rafaelperez.thirdpartyapp.R;
import com.rafaelperez.thirdpartyapp.databinding.FragmentLoggedBindingImpl;

public class LoggedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoggedBindingImpl binding = DataBindingUtil.inflate(inflater, R.layout.fragment_logged, container, false);
        LoggedFragmentArgs args = LoggedFragmentArgs.fromBundle(getArguments());
        String accessToken = args.getAccessToken();
        binding.accessToken.setText(accessToken);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }
}
