package com.rafaelperez.thirdpartyapp.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

import com.rafaelperez.thirdpartyapp.R;
import com.rafaelperez.thirdpartyapp.databinding.FragmentLoginBindingImpl;
import com.rafaelperez.thirdpartyapp.domain.AuthCodeResponse;
import com.rafaelperez.thirdpartyapp.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {
    private static final String SERVICE_PACKAGE_NAME = "com.rafaelperez.ssolauncher";
    private static final String SERVICE_CLASS_NAME = "com.rafaelperez.ssolauncher.service.RemoteService";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USERNAME = "preferred_username";
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;

    private FragmentLoginBindingImpl binding;
    private LoginViewModel viewModel;
    private Messenger messenger = null; //used to make an RPC invocation
    private boolean isBound = false;
    private ServiceConnection connection; //receives callbacks from bind and unbind invocations
    private Messenger replyTo = null; //invocation replies are processed by this Messenger
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLoginViewModel(viewModel);
        viewModel.getAuthCode().observe(getViewLifecycleOwner(), new Observer<AuthCodeResponse>() {
            @Override
            public void onChanged(AuthCodeResponse authCode) {
                if (authCode!=null) {
                    requestTokenByService(authCode);
                }
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (!error.isEmpty()) {
                    showMessage(error);
                }
            }
        });
        connection = new RemoteServiceConnection();
        replyTo = new Messenger(new IncomingHandler());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSharedPreferences();
        navigateToMainView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Bind to the remote service
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME));

        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Unbind if it is bound to the service
        if(isBound) {
            getActivity().unbindService(connection);
            isBound = false;
        }
    }

    private void getUsernameByService() {
        if(isBound) {
            Message message = Message.obtain(null, 0);
            try {
                message.replyTo = replyTo;
                messenger.send(message);
            }
            catch(RemoteException rme) {
                //Show an Error Message
                showMessage("Invocation Failed!!");
            }
        } else {
            showMessage("Service is Not Bound!!");
        }
    }

    private void requestTokenByService(AuthCodeResponse authCode) {
        if(isBound) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", "Requesting access token");
            bundle.putString("authCode", authCode.getValue());
            bundle.putLong("id", authCode.getId());
            Message message = Message.obtain(null, 1);
            message.obj = bundle;
            try {
                message.replyTo = replyTo;
                messenger.send(message);
            }
            catch(RemoteException rme) {
                showMessage("Invocation Failed!!");
            }
        } else {
            showMessage("Service is Not Bound!!");
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void setSharedPreferences() {
        prefs = getActivity().getSharedPreferences(getActivity().getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
    }

    private void saveTokenInfo(String username, String accessToken, String refreshToken) {
        prefs.edit().putString(KEY_USERNAME, username)
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    private void navigateToMainView() {
        if (prefs!=null && !prefs.getString(KEY_ACCESS_TOKEN, "").isEmpty()) {
            String accessToken = prefs.getString(KEY_ACCESS_TOKEN, "");
            Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToLoggedFragment(accessToken));
        }
    }

    private class RemoteServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName component, IBinder binder) {
            messenger = new Messenger(binder);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName component) {
            messenger = null;
            isBound = false;
        }
    }

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            System.out.println("*****************************************");
            System.out.println("Return successfully received!!!!!!");
            System.out.println("*****************************************");
            int what = message.what;
            if (what == SUCCESS) {
                Bundle bundle1 = (Bundle) message.obj;
                String accessToken = (String) bundle1.getString(KEY_ACCESS_TOKEN);
                String refreshToken = (String) bundle1.getString(KEY_REFRESH_TOKEN);
                String userName = (String) bundle1.getString(KEY_USERNAME);

                if (accessToken != null) {
                    showMessage("Successful request");
                    saveTokenInfo(userName, accessToken, refreshToken);
                    navigateToMainView();
                } else {
                    showMessage("Failed request");
                }
            } else if(what == FAIL) {
                showMessage("Invalid code");
            }
        }
    }
}
