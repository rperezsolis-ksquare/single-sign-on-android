package com.rafaelperez.singlesignon1.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.databinding.FragmentLoggedBinding;

public class LoggedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoggedBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_logged, container, false);
        return binding.getRoot();
    }
}
