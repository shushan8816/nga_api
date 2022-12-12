package com.nga.services.implementations;

import com.nga.dto.request.ChangePasswordRequest;
import com.nga.dto.request.UpdateUserRequest;
import com.nga.enums.UserStatus;
import com.nga.models.User;
import com.nga.repositories.UserRepository;
import com.nga.services.interfaces.UserService;
import com.nga.utils.EmailSender;
import com.nga.utils.exceptions.DuplicateDataException;
import com.nga.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailSender emailSender;

    @Override
    public User getById(int id) throws NotFoundException {
        log.info("Could not find user with current id: " + id);
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find user with current id: " + id));
    }

    @Override
    public User getByEmail(String email, UserStatus status) {
        return userRepository.findByEmail(email, status);
    }

    @Override
    public boolean existsByVerificationCode(String verificationCode) {
        return userRepository.existsByVerificationCode(verificationCode);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllWithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public void save(User user) throws DuplicateDataException {
        String email = user.getEmail();

        if (this.getByEmail(email, null) != null) {
            log.info("An account with email: {} already exists", email);
            throw new DuplicateDataException("An account with current email already exists");
        }

        String verificationCode = this.generateVerificationCode();

        while (this.existsByVerificationCode(verificationCode)) {
            verificationCode = this.generateVerificationCode();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.UNVERIFIED);
        user.setVerificationCode(verificationCode);

        log.info("Saving new user to the database");
        userRepository.save(user);

        log.info("Send a message to email");
        emailSender.sendSimpleMessage(email, verificationCode, "");

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

    public void saveWithNewVerificationCode(User user) {

        String verificationCode = this.generateVerificationCode();

        while (this.existsByVerificationCode(verificationCode)) {
            verificationCode = this.generateVerificationCode();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(verificationCode);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public User verifyAndChangePassword(ChangePasswordRequest request) throws NotFoundException {
        User user = userRepository
                .findByEmailAndVerificationCodeAndStatus(request.getEmail(), request.getVerificationCode(), UserStatus.UNVERIFIED)
                .orElseThrow(()-> new NotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ENABLE);

        return userRepository.save(user);
    }


    @Transactional
    @Override
    public void update(UpdateUserRequest request) throws NotFoundException {
        int userId = request.getId();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();

        User user = this.getById(userId);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

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


    public String generateVerificationCode() {
        Random randomString = new Random();

        return randomString.ints(48, 123)
                .filter(num -> (num < 58 || num > 64) && (num < 91 || num > 96))
                .limit(15)
                .mapToObj(c -> (char) c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();
    }
}
