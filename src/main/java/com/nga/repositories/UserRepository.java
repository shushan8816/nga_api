package com.nga.repositories;

import com.nga.enums.UserStatus;
import com.nga.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.status = ?2")
    User findByEmailAndStatus(String email, UserStatus status);

    Optional<User> getByEmail(String email);

    Optional<User> findByEmailAndVerificationCodeAndStatus(String email, String verificationCode, UserStatus status);

    boolean existsByVerificationCode(String verificationCode);

    boolean existsByResetPasswordToken(String resetToken);

    Optional<User> findByResetPasswordTokenAndEmail(String resetToken, String email);


}
