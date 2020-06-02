package com.rafaelperez.ssolauncher.authentication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;

public class AppAuthHelper {

    //Our sso impl
    //10.0.2.2 is the alias for the local machine localhost
    //private static final String URL_AUTHORIZATION = "http://10.0.2.2:8080/oauth/authorize";
    //private static final String URL_TOKEN = "http://10.0.2.2:8080/oauth/token";
    //private static final String URL_REDIRECT = "http://localhost:8080/dashboard";
    //private static final String CLIENT_ID = "defaultclient";
    //private static final String CLIENT_SECRET = "KsquareChangeThis";

    //Key Cloak
    private static final String URL_AUTHORIZATION = "https://sso.theksquaregroup.com/auth/realms/ksquare/protocol/openid-connect/auth";
    private static final String URL_TOKEN = "https://sso.theksquaregroup.com/auth/realms/ksquare/protocol/openid-connect/token";
    private static final String URL_REDIRECT = "http://localhost";
    private static final String CLIENT_ID = "this-is-a-client-id-of-keycloak";
    private static final String CLIENT_SECRET = "bc51b6f4-1dd0-499e-96b6-bc908ea8865a";
    private static final String TOKEN_SCOPE = "email profile openid";

    //Google
    /*private static final String URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String URL_TOKEN = "https://www.googleapis.com/oauth2/v4/token";
    private static final String URL_REDIRECT = "com.google.codelabs.appauth:/oauth2callback";
    private static final String CLIENT_ID = "511828570984-fuprh0cm7665emlne3rnf9pk34kkn86s.apps.googleusercontent.com";
    private static final String TOKEN_SCOPE = "profile";*/

    private AuthorizationService authService;
    private ClientAuthentication clientAuthentication;
    private AuthorizationServiceConfiguration serviceConfig;

    public AppAuthHelper(Context context) {
        authService = new AuthorizationService(context);
        clientAuthentication = new ClientSecretBasic(CLIENT_SECRET);
        setServiceConfig();
    }

    public Intent getAuthIntent() {
        return authService.getAuthorizationRequestIntent(getAuthRequest());
    }

    private AuthorizationRequest getAuthRequest() {
        return new AuthorizationRequest.Builder(
                getServiceConfig(), // the authorization service configuration
                CLIENT_ID, // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE, // the response_type value: we want a code
                Uri.parse(URL_REDIRECT)).
                setScope(TOKEN_SCOPE)
                .build();
    }

    private AuthorizationServiceConfiguration getServiceConfig() {
        return new AuthorizationServiceConfiguration(Uri.parse(URL_AUTHORIZATION), Uri.parse(URL_TOKEN));
        //return serviceConfig;
    }

    public AuthorizationService getAuthService() {
        return authService;
    }

    public ClientAuthentication getClientAuthentication() {
        return clientAuthentication;
    }

    private void setServiceConfig() {
        if (serviceConfig==null) {
            AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveConfigurationCallback =
                    new AuthorizationServiceConfiguration.RetrieveConfigurationCallback() {
                        @Override
                        public void onFetchConfigurationCompleted(
                                @Nullable AuthorizationServiceConfiguration serviceConfiguration,
                                @Nullable AuthorizationException ex) {
                            if (ex != null) {
                                return;
                            }
                            setServiceConfig(serviceConfiguration);
                        }
                    };
            AuthorizationServiceConfiguration.fetchFromIssuer(
                    Uri.parse("https://sso.theksquaregroup.com"),
                    retrieveConfigurationCallback);
        }
    }

    private void setServiceConfig(AuthorizationServiceConfiguration serviceConfig) {
        this.serviceConfig = serviceConfig;
    }
}