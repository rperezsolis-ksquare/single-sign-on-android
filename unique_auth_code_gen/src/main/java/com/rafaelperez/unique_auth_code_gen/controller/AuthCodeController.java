package com.rafaelperez.unique_auth_code_gen.controller;

import java.util.Map;

import javax.security.auth.message.AuthStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafaelperez.unique_auth_code_gen.domain.AuthCode;
import com.rafaelperez.unique_auth_code_gen.domain.ErrorResponse;
import com.rafaelperez.unique_auth_code_gen.domain.ServiceResponse;
import com.rafaelperez.unique_auth_code_gen.domain.ValidateAuthCodeRequest;
import com.rafaelperez.unique_auth_code_gen.service.AuthCodeServiceImpl;
import com.rafaelperez.unique_auth_code_gen.service.AuthenticationServiceImpl;

@RestController
@RequestMapping("api/authCode")
public class AuthCodeController {

	@Autowired
	private AuthCodeServiceImpl authCodeService;
	
	@Autowired
	private AuthenticationServiceImpl authenticationService;
	
	/*
	 *	Simulates the process of generating a code  
	 * */
	@PostMapping(value = "/getAuthCode", consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAuthCode(@RequestBody Map<String, String> clientCredentials) {
		String clientId = clientCredentials.get("clientId");
		String clientSecret = clientCredentials.get("clientSecret");
		if(authenticationService.isAllowedClient(clientId, clientSecret)) {
			String code = authCodeService.generateCode();
			authCodeService.setAuthCode(code);
			authCodeService.setCodeId(authCodeService.getCodeId()+1);
			AuthCode authCode = new AuthCode(authCodeService.getCodeId(), authCodeService.getAuthCode());
			return ResponseEntity.ok().body(authCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized client"));
		}
	}

	/*
	 *	Simulates the process of validating a code  
	 * */
	@PostMapping(value = "/validateAuthCode", consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateAuthCode(@RequestBody ValidateAuthCodeRequest request) {
		String clientId = request.getClientId();
		String clientSecret = request.getClientSecret();
		String code = request.getAuthCode();
		Long codeId = request.getId();
		if(authenticationService.isAllowedClient(clientId, clientSecret)) {
			if(authCodeService.isValidCode(code, codeId)) {
				authCodeService.updateCode(codeId);
				return ResponseEntity.ok().body(new ServiceResponse("Valid code"));
			} else {
				return ResponseEntity.ok().body(new ServiceResponse("Invalid code"));
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized client"));
		}
	}
}
