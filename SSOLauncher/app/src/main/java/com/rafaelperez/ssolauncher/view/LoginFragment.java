package com.rafaelperez.ssolauncher.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
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
import com.rafaelperez.ssolauncher.R;
import com.rafaelperez.ssolauncher.authentication.AppAuthHelper;
import com.rafaelperez.ssolauncher.databinding.FragmentLoginBindingImpl;
import com.rafaelperez.ssolauncher.utils.JWTUtils;
import com.rafaelperez.ssolauncher.viewmodel.LoginViewModel;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private static final int AUTH_CODE_REQ = 10;
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ID_TOKEN_USERNAME = "preferred_username";

    private LoginViewModel viewModel;
    private FragmentLoginBindingImpl binding;
    private AppAuthHelper appAuthHelper;
    private AccountManager accountManager;
    private int readAccountsPermission;
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
        appAuthHelper = new AppAuthHelper(getContext());
        accountManager = AccountManager.get(getContext());
        updatePermissionStatus();
        navigateToMainView();
    }

    private void getToken() {
        if (readAccountsPermission == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if (accounts.length==0) {
                final Intent authIntent = appAuthHelper.getAuthIntent();
                startActivityForResult(authIntent, AUTH_CODE_REQ);
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
        readAccountsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== Activity.RESULT_OK) {
            if (requestCode == AUTH_CODE_REQ) {
                AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(data);
                AuthorizationException authException = AuthorizationException.fromIntent(data);
                if (authResponse != null) {
                    appAuthHelper.getAuthService().performTokenRequest(
                            authResponse.createTokenExchangeRequest(),
                            appAuthHelper.getClientAuthentication(),
                            new AuthorizationService.TokenResponseCallback() {
                                @Override public void onTokenRequestCompleted(
                                        TokenResponse resp, AuthorizationException ex) {
                                    if (resp != null) {
                                        String accessToken = resp.accessToken;
                                        String refreshToken = resp.refreshToken;
                                        String idToken = JWTUtils.decode(resp.idToken);
                                        try {
                                            JSONObject jsonObject = new JSONObject(idToken);
                                            String username = jsonObject.getString(KEY_ID_TOKEN_USERNAME);
                                            addAccount(username, getString(R.string.account_type), accessToken, refreshToken);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        navigateToMainView();
                                    } else {
                                        showMsg(ex.errorDescription);
                                    }
                                }
                            });
                } else {
                    showMsg(authException.errorDescription);
                }
            }
        } else {
            showMsg(getString(R.string.login_error));
        }
    }

    private void addAccount(String username, String accountType, String accessToken, String refreshToken) {
        Account account = new Account(username,accountType);
        accountManager.addAccountExplicitly(account, "", null);
        accountManager.setUserData(account, AccountManager.KEY_AUTHTOKEN, accessToken);
        accountManager.setUserData(account, KEY_REFRESH_TOKEN, refreshToken);
    }

    private void showMsg(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void navigateToMainView() {
        if (readAccountsPermission == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if (accounts.length>1) {
                selectAccount(accounts);
            } else if (accounts.length==1) {
                String accessToken = accountManager.getUserData(accounts[0], AccountManager.KEY_AUTHTOKEN);
                Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
            } else {
                showMsg("No account available");
                //todo: erase this, as is only for testing when the server is not available
                Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment("accessToken"));
            }
        } else {
            requestPermission();
        }
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
                String accessToken = accountManager.getUserData(accounts[which], AccountManager.KEY_AUTHTOKEN);
                Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
                dialog.dismiss();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.sym_def_app_icon)
                .show();
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