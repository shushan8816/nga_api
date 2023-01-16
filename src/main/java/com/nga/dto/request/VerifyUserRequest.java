package com.nga.dto.request;

import com.nga.enums.UserStatus;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class VerifyUserRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationCode;

    @NotBlank
    private UserStatus status;
}
