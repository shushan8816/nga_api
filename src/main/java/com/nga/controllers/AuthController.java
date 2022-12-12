package com.nga.controllers;

import com.nga.dto.request.AuthenticationRequest;
import com.nga.services.interfaces.AuthService;
import com.nga.utils.exceptions.JwtAuthenticationException;
import com.nga.utils.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request) throws NotFoundException, JwtAuthenticationException {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
