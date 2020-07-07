package com.rafaelperez.singlesignon1.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.databinding.FragmentLoginBindingImpl;
import com.rafaelperez.singlesignon1.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {
    private static final int AUTH_CODE_REQ = 10;
    private static final int SIGN_IN_REQ = 20;
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String AUTHENTICATOR_PACKAGE = "com.rafaelperez.ssolauncher";

    private LoginViewModel viewModel;
    private FragmentLoginBindingImpl binding;
    private AccountManager accountManager;
    private int permissionCheck;
    private final int[] selectedAccountIndex = {0};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLoginViewModel(viewModel);
        viewModel.getSigningIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean signingIn) {
                if (signingIn) {
                    getToken();
                }
            }
        });
        viewModel.getAccessDenied().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean accessDenied) {
                if (accessDenied) {
                    getToken();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountManager = AccountManager.get(getContext());
        updatePermissionStatus();
        navigateToMainView();
    }

    private void navigateToMainView() {
        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if (accounts.length>1) {
                selectAccount(accounts);
            } else if (accounts.length==1) {
                String accessToken = accountManager.getUserData(accounts[0], KEY_ACCESS_TOKEN);
                Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
            } else {
                showMsg("No account available");
            }
        } else {
            requestPermission();
        }
    }

    private void getToken() {
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if (accounts.length==0) {
                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(AUTHENTICATOR_PACKAGE);
                //startActivity(launchIntent);
                startActivityForResult(launchIntent, SIGN_IN_REQ);
            } else {
                navigateToMainView();
            }
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
    }

    private void updatePermissionStatus() {
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
    }

    private void showMsg(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void selectAccount(Account[] accounts) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice);
        for (Account account : accounts) {
            arrayAdapter.add(account.name);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle("Select account");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedAccountIndex[0] = which;
                String accessToken = accountManager.getUserData(accounts[which], KEY_ACCESS_TOKEN);
                Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
                dialog.dismiss();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.sym_def_app_icon).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==SIGN_IN_REQ) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if (accounts.length==0) {
                showMsg(getString(R.string.login_error));
            } else {
                showMsg(getString(R.string.login_retry_please));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updatePermissionStatus();
                showMsg("Permission granted to read your accounts info");
            } else {
                showMsg("Permission denied to read your accounts info");
            }
        }
    }
}