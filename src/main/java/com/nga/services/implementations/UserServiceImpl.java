package com.nga.services.implementations;

import com.nga.dto.request.*;
import com.nga.enums.UserStatus;
import com.nga.models.User;
import com.nga.repositories.UserRepository;
import com.nga.services.interfaces.UserService;
import com.nga.utils.exceptions.DuplicateDataException;
import com.nga.utils.exceptions.InternalErrorException;
import com.nga.utils.exceptions.NotFoundException;
import com.nga.utils.helper.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final long EXPIRED_TIME_MILLIS = 24 * 60 * 60 * 1000;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHelper userHelper;

    @Override
    public User getById(int id) throws NotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Could not found user with current id: " + id));
    }

    @Override
    public User findByEmail(String email, UserStatus status) {
        return userRepository.findByEmailAndStatus(email, status);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllWithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public boolean existsByVerificationCode(String verificationCode) {
        return userRepository.existsByVerificationCode(verificationCode);
    }

    @Transactional
    @Override
    public void save(User user) throws DuplicateDataException {
        String email = user.getEmail();

        if (this.findByEmail(email, null) != null) {
            log.info("An account with email: {} already exists", email);
            throw new DuplicateDataException("An account with current email already exists");
        }

        String verificationCode = userHelper.generateVerificationCode();

        while (this.existsByVerificationCode(verificationCode)) {
            verificationCode = userHelper.generateVerificationCode();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.UNVERIFIED);
        user.setVerificationCode(verificationCode);

        String token = RandomStringUtils.randomAlphanumeric(8);
        user.setResetPasswordToken(token);

        log.info("Saving new user to the database");
        userRepository.save(user);

        log.info("Send a message to email");
        userHelper.sendSimpleMessage(email, verificationCode, "Confirm your account");
    }

    @Transactional
    @Override
    public void saveAll(List<User> users) {

        List<User> userList = users
                .stream()
                .peek(this::saveWithNewVerificationCode)
                .collect(Collectors.toList());

        log.info("Saving all users to the database");
        userRepository.saveAll(userList);
    }

    private void saveWithNewVerificationCode(User user) {

        String verificationCode = userHelper.generateVerificationCode();

        while (this.existsByVerificationCode(verificationCode)) {
            verificationCode = userHelper.generateVerificationCode();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(verificationCode);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws NotFoundException {
        User user = userRepository
                .getByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Could not found an account with current email address."));

        String token = RandomStringUtils.randomAlphanumeric(8);

        while (userRepository.existsByResetPasswordToken(token)) {
            token = RandomStringUtils.randomAlphanumeric(8);
        }

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiredTime(System.currentTimeMillis() + EXPIRED_TIME_MILLIS);

        userHelper.sendSimpleMessage(user.getEmail(), user.getVerificationCode(), "Confirmation code: ");
    }


    @Transactional
    @Override
    public void resendPassword(ResetPasswordRequest request) throws NotFoundException, InternalErrorException {
        User user = userRepository
                .findByResetPasswordTokenAndEmail(request.getResetToken(), request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found in DB. Invalid token or email!"));

        if (request.getResetPasswordExpiredTime() <= System.currentTimeMillis()) {
            log.error("Failed to token reset process");
            throw new InternalErrorException("Failed to token reset process. Reset token is expired");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiredTime(null);

        userRepository.save(user);
        userHelper.sendSimpleMessage(user.getEmail(), null, "Password successfully resend. ");
    }

    @Transactional
    public User verifyUser(VerifyUserRequest request) throws NotFoundException, InternalErrorException {
        User user = userRepository
                .findByEmailAndVerificationCodeAndStatus(request.getEmail(), request.getVerificationCode(), UserStatus.UNVERIFIED)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getVerificationCodeExpiredTime() <= System.currentTimeMillis()) {
            log.error("Failed to token reset process");
            throw new InternalErrorException("Failed to token reset process. Reset token is expired");
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiredTime(null);


        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void resendVerificationCode(ResendVerificationCodeRequest request) throws NotFoundException, InternalErrorException {
        User user = userRepository
                .getByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getVerificationCodeExpiredTime() <= System.currentTimeMillis()) {
            log.error("Failed to token reset process");
            throw new InternalErrorException("Failed to token reset process. Reset token is expired");
        }
        String verificationCode = userHelper.generateVerificationCode();

        while (this.existsByVerificationCode(verificationCode)) {
            verificationCode = userHelper.generateVerificationCode();
        }
        user.setVerificationCode(request.getVerificationCode());
        user.setVerificationCodeExpiredTime(System.currentTimeMillis() + EXPIRED_TIME_MILLIS);

        userRepository.save(user);
        userHelper.sendSimpleMessage(user.getEmail(), null, "Verification code successfully resend. ");
    }

    @Transactional
    @Override
    public void update(UpdateUserRequest request) throws NotFoundException {
        User user = this.getById(request.getId());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        log.info("Changing information for User: {}", user);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(int id) throws NotFoundException {
        this.getById(id);

        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}
