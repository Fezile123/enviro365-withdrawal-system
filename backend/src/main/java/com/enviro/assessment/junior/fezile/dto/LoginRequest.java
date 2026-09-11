package com.enviro.assessment.junior.fezile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * No password by design -- this is a simple "who am I" lookup, not real
 * authentication. Enough to demo the app as a specific investor without
 * building a full security layer, which is out of scope for this
 * assessment (not part of the brief's requirements).
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "email is required")
    @Email(message = "must be a valid email address")
    private String email;
}
