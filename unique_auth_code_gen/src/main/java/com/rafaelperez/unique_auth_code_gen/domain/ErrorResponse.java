package com.rafaelperez.unique_auth_code_gen.domain;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String error = "";

    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
