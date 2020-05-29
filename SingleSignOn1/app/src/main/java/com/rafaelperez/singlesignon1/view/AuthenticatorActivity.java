package com.rafaelperez.singlesignon1.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rafaelperez.singlesignon1.network.OAuth2TokenHelper;

import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;

import java.net.URI;

public class AuthenticatorActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    public final static String AUTH_TOKEN = "authToken";
    public final static String REFRESH_TOKEN = "refreshToken";
    private final int REQ_SIGNUP = 1;
    private final String TAG = this.getClass().getSimpleName();

    private WebView webView;
    private int loginFormSubmited = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        signIn();
    }

    private void signIn() {
        OAuth2TokenHelper tokenHelper = new OAuth2TokenHelper();
        OAuth2InteractiveGrant grant = tokenHelper.getInteractiveGrant();
        URI authUrl = tokenHelper.getAuthUrl(grant);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearCache(true);
        webView.setWebViewClient(new MyWebViewClient(tokenHelper, grant));
        webView.loadUrl(authUrl.toString());
        //webView.loadUrl("http://www.google.com");
    }

    private class MyWebViewClient extends WebViewClient{

        private  OAuth2TokenHelper tokenHelper;
        private OAuth2InteractiveGrant grant;

        MyWebViewClient(OAuth2TokenHelper tokenHelper, OAuth2InteractiveGrant grant) {
            this.tokenHelper = tokenHelper;
            this.grant = grant;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request.getUrl().toString().contains("login")) {
                    return false;
                }
            }
            return true;*/
            return false;
        }
//http://10.0.2.2:8080/login
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loginFormSubmited++;
            if (loginFormSubmited >1 && !url.contains("error")) {
                TokenAsynTask tokenAsynTask = new TokenAsynTask(tokenHelper, grant, url);
                tokenAsynTask.execute();
            }
        }
    }

    private class TokenAsynTask extends AsyncTask<String, Void, Intent> {

        private OAuth2TokenHelper tokenHelper;
        private OAuth2InteractiveGrant grant;
        private String url;

        public TokenAsynTask(OAuth2TokenHelper tokenHelper, OAuth2InteractiveGrant grant, String url) {
            this.tokenHelper = tokenHelper;
            this.grant = grant;
            this.url = url;
        }

        @Override
        protected Intent doInBackground(String... strings) {
            Bundle data = new Bundle();
            try {
                OAuth2AccessToken oAuth2AccessToken = grant.withRedirect(tokenHelper.getUri(url)).accessToken(tokenHelper.getExecutor());
                String authToken = oAuth2AccessToken.accessToken().toString();
                String refreshToken = oAuth2AccessToken.refreshToken().toString();
                data.putString(AUTH_TOKEN, authToken);
                data.putString(REFRESH_TOKEN, refreshToken);
            } catch (Exception e) {
                data.putString(KEY_ERROR_MESSAGE, e.getMessage());
            }
            final Intent intent = new Intent();
            intent.putExtras(data);
            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                showErrorMessage(intent);
            } else {
                finishLogin(intent);
            }
        }
    }

    private void finishLogin(Intent intent) {
        if (intent.getStringExtra(AUTH_TOKEN)!=null && !intent.getStringExtra(AUTH_TOKEN).isEmpty()) {
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    private void showErrorMessage(Intent intent) {
        Toast.makeText(this, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_LONG).show();
    }
}
