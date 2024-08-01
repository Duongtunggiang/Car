package com.group.car.models.Dto;

import jakarta.validation.constraints.*;

public class RegisterDto {

    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @Size(min = 8, message = "Minium password length 8 characters")
    private String password;

    @Size( message = "Password and Confirm password do not match")
    private String confirmPassword;

    private boolean enabled ;

    public @NotEmpty String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty String username) {
        this.username = username;
    }

    public @NotEmpty @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty @Email String email) {
        this.email = email;
    }

    public @Size(min = 8, message = "Minium password length 8 characters") String getPassword() {
        return password;
    }

    public void setPassword(@Size(min = 8, message = "Minium password length 8 characters") String password) {
        this.password = password;
    }

    public @Size(message = "Password and Confirm password do not match") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@Size(message = "Password and Confirm password do not match") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
