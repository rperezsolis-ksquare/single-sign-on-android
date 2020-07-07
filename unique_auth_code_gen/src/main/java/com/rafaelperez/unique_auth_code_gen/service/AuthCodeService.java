package com.rafaelperez.unique_auth_code_gen.service;

public interface AuthCodeService {
	
	public String generateCode();
	
	public void updateCode(Long id);
	
	public boolean isValidCode(String authCodeValue, Long codeId);
}
