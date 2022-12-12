package com.nga.services.interfaces;


import com.nga.dto.request.AuthenticationRequest;
import com.nga.utils.exceptions.JwtAuthenticationException;
import com.nga.utils.exceptions.NotFoundException;

import java.util.Map;

public interface AuthService {
    Map<String, Object> authenticate(AuthenticationRequest request) throws JwtAuthenticationException, NotFoundException;
}
