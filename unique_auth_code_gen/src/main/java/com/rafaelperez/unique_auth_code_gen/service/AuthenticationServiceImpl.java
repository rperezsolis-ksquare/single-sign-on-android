package com.rafaelperez.unique_auth_code_gen.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Map<String, String> allowedClients = new HashMap<String, String>(){
		{
			put("third_party_app", "123456789");
			put("sso_launcher", "123456789");
		}
	};

	/*
	 *	Simulates the process of validate the client
	 * */
	@Override
	public boolean isAllowedClient(String clientId, String clientSecret) {
		String value = allowedClients.get(clientId);
		return value!=null && !value.isEmpty() && clientSecret.equals(value); 
	}

}
