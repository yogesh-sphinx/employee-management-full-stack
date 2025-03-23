package com.employee_management.responses;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }
    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

}
