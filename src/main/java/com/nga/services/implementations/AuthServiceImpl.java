package com.nga.services.implementations;


import com.nga.dto.request.AuthenticationRequest;
import com.nga.enums.JwtTokenType;
import com.nga.enums.UserStatus;
import com.nga.models.User;
import com.nga.security.JwtTokenProvider;
import com.nga.services.interfaces.AuthService;
import com.nga.services.interfaces.UserService;
import com.nga.utils.exceptions.JwtAuthenticationException;
import com.nga.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public Map<String, Object> authenticate(AuthenticationRequest request) throws JwtAuthenticationException, NotFoundException {
        String email, password, role;
        int userId;

        email = request.getEmail();
        User user = userService.getByEmail(email, UserStatus.ENABLE);

        if (user == null) {
            throw new NotFoundException("User with current email:" + email + " is not found");
        }

        password = request.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.info("Password is not valid");
            throw new JwtAuthenticationException("Incorrect email or password", HttpStatus.UNAUTHORIZED);
        }

        userId = user.getId();
        role = user.getAuthority().getRole();

        String accessToken = jwtTokenProvider.generateToken(userId, role, email, JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.generateToken(userId, role, email, JwtTokenType.REFRESH_TOKEN);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("role", role);
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;

    }
}

