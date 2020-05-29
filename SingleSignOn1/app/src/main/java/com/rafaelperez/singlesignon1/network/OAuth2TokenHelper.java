package com.rafaelperez.singlesignon1.network;

import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;
import org.dmfs.oauth2.client.grants.AuthorizationCodeGrant;
import org.dmfs.oauth2.client.grants.ResourceOwnerPasswordGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.Duration;

import java.io.IOException;
import java.net.URI;

public class OAuth2TokenHelper {

    //10.0.2.2 is the alias for the local machine localhost
    private static final String URL_AUTHORIZE = "http://10.0.2.2:8080/oauth/authorize";
    private static final String URL_TOKEN = "http://10.0.2.2:8080/oauth/token";
    private static final String URL_REDIRECT = "http://localhost:8080/dashboard";
    private static final String CLIENT_ID = "defaultclient";
    private static final String CLIENT_SECRET = "KsquareChangeThis";
    private static final String TOKEN_SCOPE = "read write trust";

    private HttpRequestExecutor executor;
    private OAuth2Client client;
    private OAuth2AccessToken token;

    public OAuth2TokenHelper() {
        executor = new HttpUrlConnectionExecutor();
        // Create OAuth2 provider
        OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
                URI.create(URL_AUTHORIZE),
                URI.create(URL_TOKEN),
                new Duration(1,0,3600));
        // Create OAuth2 client credentials
        OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(CLIENT_ID, CLIENT_SECRET);
        // Create OAuth2 client
        client = new BasicOAuth2Client(
                provider,
                credentials,
                new LazyUri(new Precoded(URL_REDIRECT)) /* Redirect URL */);
    }

    public OAuth2InteractiveGrant getInteractiveGrant() {
        return new AuthorizationCodeGrant(client, new BasicScope(TOKEN_SCOPE));
    }

    public URI getAuthUrl(OAuth2InteractiveGrant grant) {
        return grant.authorizationUrl();
    }

    public LazyUri getUri(String url) {
        return new LazyUri(new Precoded(url));
    }

    public HttpRequestExecutor getExecutor() {
        return executor;
    }

    public OAuth2AccessToken getTokenUsingPasswordCredentials(String userName, String userPass) {
        try {
            token = new ResourceOwnerPasswordGrant(
                    client,
                    new BasicScope(TOKEN_SCOPE),
                    userName,
                    userPass).accessToken(executor);
        } catch (IOException | ProtocolError | ProtocolException e) {
            e.printStackTrace();
        }
        return token;
    }
}
