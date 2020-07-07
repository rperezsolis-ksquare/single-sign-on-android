package com.rafaelperez.unique_auth_code_gen.service;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

@Service("authCodeService")
public class AuthCodeServiceImpl implements AuthCodeService {

	private String authCode = "";
	private String username = "";
	private Long codeId = 1l;
	
	@Override
	public String generateCode() {
		SecureRandom random = new SecureRandom();
	    byte[] bytes = new byte[20];
	    random.nextBytes(bytes);
	    Base32 base32 = new Base32();
		return base32.encodeToString(bytes);
	}

	@Override
	public void updateCode(Long id) {
		authCode = generateCode();
		System.out.println(authCode);
	}

	@Override
	public boolean isValidCode(String authCodeValue, Long codeId) {
		return authCodeValue.equals(getAuthCode()) && codeId==getCodeId();
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
}
