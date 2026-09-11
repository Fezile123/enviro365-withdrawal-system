package com.enviro.assessment.junior.fezile.controller;

import com.enviro.assessment.junior.fezile.dto.LoginRequest;
import com.enviro.assessment.junior.fezile.dto.RegisterRequest;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Investor login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getEmail());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Investor register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
