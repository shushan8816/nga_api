package com.nga.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nga.enums.UserStatus;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    @JsonIgnore
    private String verificationCode;

    @Column(nullable = false)
    private UserStatus status;

    @OneToOne
    @JoinColumn(nullable = false)
    private Authority authority;

    @JsonIgnore
    @Column(unique = true)
    private String resetPasswordToken;

    @JsonIgnore
    private Long resetPasswordExpiredTime;

    @JsonIgnore
    private Long verificationCodeExpiredTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() && Objects.equals(getFirstName(), user.getFirstName()) && Objects.equals(getLastName(), user.getLastName()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getVerificationCode(), user.getVerificationCode()) && getStatus() == user.getStatus() && Objects.equals(getAuthority(), user.getAuthority()) && Objects.equals(getResetPasswordToken(), user.getResetPasswordToken()) && Objects.equals(getResetPasswordExpiredTime(), user.getResetPasswordExpiredTime()) && Objects.equals(getVerificationCodeExpiredTime(), user.getVerificationCodeExpiredTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getEmail(), getPassword(), getVerificationCode(), getStatus(), getAuthority(), getResetPasswordToken(), getResetPasswordExpiredTime(), getVerificationCodeExpiredTime());
    }
}
