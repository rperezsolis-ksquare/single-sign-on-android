package com.rafaelperez.singlesignon1.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.databinding.FragmentLoginBinding;
import com.rafaelperez.singlesignon1.viewmodel.LoginViewModel;


public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLoginViewModel(viewModel);
        viewModel.get_signingIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean signingIn) {
                if (signingIn) {
                    viewModel.authenticate();
                    //testOnChanged();
                }
            }
        });


        return binding.getRoot();
    }

    private void testOnChanged() {
        Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment());
    }
}
