package com.rafaelperez.singlesignon1.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.databinding.FragmentLoginBinding;
import com.rafaelperez.singlesignon1.network.AppAuthHelper;
import com.rafaelperez.singlesignon1.utils.JWTUtils;
import com.rafaelperez.singlesignon1.viewmodel.LoginViewModel;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {

    private static final int AUTH_CODE_REQ = 10;
    private static final String KEY_REFRESH_TOKEN = "refresh_token";


    private LoginViewModel viewModel;
    private FragmentLoginBinding binding;
    private AppAuthHelper appAuthHelper;
    private AccountManager accountManager;
    private final int[] selectedAccountIndex = {0};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        /*try {
            con = createPackageContext("com.sharedpref1", 0);//first app package name is "com.sharedpref1"
            SharedPreferences pref = con.getSharedPreferences(
                        "demopref", Context.MODE_PRIVATE);
            String your_data = pref.getString("demostring", "No Value");
        }
    catch (NameNotFoundException e) {
                Log.e("Not data shared", e.toString());
         }*/

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
        navigateToMainView();
    }

    private void getToken() {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if (accounts.length==0) {
            final Intent authIntent = appAuthHelper.getAuthIntent();
            startActivityForResult(authIntent, AUTH_CODE_REQ);
        } else {
            showMsg("There are accounts");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         if (resultCode==Activity.RESULT_OK) {
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
                                         String username = jsonObject.getString("preferred_username");
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

    private void storeToken(String token) {
        SharedPreferences prefs = getActivity().getSharedPreferences("ssoToken",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("refreshToken", token);
        editor.apply();
    }

    private void navigateToMainView() {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if (accounts.length>1) {
            selectAccount(accounts);
        } else if (accounts.length==1) {
            String accessToken = accountManager.getUserData(accounts[0], AccountManager.KEY_AUTHTOKEN);
            Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
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
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setIcon(android.R.drawable.sym_def_app_icon)
        .show();
    }
}
