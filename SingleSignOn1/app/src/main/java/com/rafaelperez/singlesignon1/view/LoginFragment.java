package com.rafaelperez.singlesignon1.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.authentication.AccountGeneral;
import com.rafaelperez.singlesignon1.authentication.SsoAuthenticator;
import com.rafaelperez.singlesignon1.databinding.FragmentLoginBinding;
import com.rafaelperez.singlesignon1.viewmodel.LoginViewModel;

import java.io.IOException;


public class LoginFragment extends Fragment {

    private static int CREDENTIALS_RESQUEST_CODE = 1;
    private static int ACCOUNT_MANAGER_CALLBACK_REQUEST_CODE = 0;

    private LoginViewModel viewModel;
    private FragmentLoginBinding binding;
    private AccountManager accountManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        accountManager = AccountManager.get(getContext());
        binding.setLoginViewModel(viewModel);
        viewModel.getSigningIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean signingIn) {
                if (signingIn) {
                    getToken();
                    //testOnChanged();
                }
            }
        });
        viewModel.getAccessDenied().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean accessDenied) {
                if (accessDenied) {
                    Intent intent = new Intent(getContext(), AuthenticatorActivity.class);
                    startActivityForResult(intent, CREDENTIALS_RESQUEST_CODE);
                }
            }
        });

        return binding.getRoot();
    }

    private void getToken() {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if (accounts.length==0) {
            //If there is no account, add a new one using user credentials
            final Intent intent = new Intent(getContext(), AuthenticatorActivity.class);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivityForResult(intent, CREDENTIALS_RESQUEST_CODE);
        }
        //todo: If there's more than one Account in the array, present a dialog asking the user to select one and use that one to get the token.
        Bundle options = new Bundle();
        //todo: Define the token type.
        accountManager.getAuthToken(accounts[0],              // Account retrieved using getAccountsByType()
                AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,    // Auth scope (token type)
                options,                                      // Authenticator-specific options
                getActivity(),                                // Your activity
                new OnTokenAcquired(),                        // Callback called when a token is successfully acquired
                new Handler(new Handler.Callback() { // Callback called if an error occurs
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        return false;
                    }
                })
        );
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, ACCOUNT_MANAGER_CALLBACK_REQUEST_CODE);
                    return;
                }
                viewModel.authenticate(token);
            } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK) {
            if (requestCode==CREDENTIALS_RESQUEST_CODE && data!=null) {
                String token = data.getStringExtra("token");
                viewModel.authenticate(token);
            } else {
                getToken();
            }
        }
    }


    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = accountManager.addAccount(accountType, authTokenType,
                null, null, getActivity(), new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    showMessage("Account was created");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }, null);
    }

    private void showMessage(final String msg) {
        if (msg.isEmpty()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void testOnChanged() {
        Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment());
    }
}
