package com.group.car.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterDto {

    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @Size(min = 8, message = "Minium password length 8 characters")
    private String password;

    private String confirmPassword;

    private boolean enabled ;

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public @NotEmpty String getUsername() {
        return username;
    }

    public @NotEmpty @Email String getEmail() {
        return email;
    }

    public @Size(min = 8, message = "Minium password length 8 characters") String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(@NotEmpty String username) {
        this.username = username;
    }

    public void setEmail(@NotEmpty @Email String email) {
        this.email = email;
    }

    public void setPassword(@Size(min = 8, message = "Minium password length 8 characters") String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
