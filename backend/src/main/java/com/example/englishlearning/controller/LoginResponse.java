package com.example.englishlearning.controller;

public class LoginResponse {

    private String email;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
