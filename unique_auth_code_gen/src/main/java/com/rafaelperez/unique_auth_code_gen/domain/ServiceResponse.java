package com.rafaelperez.unique_auth_code_gen.domain;

import java.io.Serializable;

public class ServiceResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String response = "";

    public ServiceResponse() {
    }

    public ServiceResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
