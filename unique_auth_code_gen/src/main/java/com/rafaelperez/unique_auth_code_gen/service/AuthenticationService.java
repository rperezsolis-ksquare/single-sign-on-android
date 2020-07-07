package com.rafaelperez.unique_auth_code_gen.service;

public interface AuthenticationService {
	
	public boolean isAllowedClient(String clientId, String clientSecret);
}
