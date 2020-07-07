package com.rafaelperez.unique_auth_code_gen.domain;

import java.io.Serializable;

public class AuthCode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String value;
	
	public AuthCode() {}
	
	public AuthCode(Long id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
