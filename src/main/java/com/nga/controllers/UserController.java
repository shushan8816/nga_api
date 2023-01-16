package com.nga.controllers;

import com.nga.dto.request.*;
import com.nga.models.User;
import com.nga.services.interfaces.UserService;
import com.nga.utils.exceptions.BadRequestException;
import com.nga.utils.exceptions.DuplicateDataException;
import com.nga.utils.exceptions.InternalErrorException;
import com.nga.utils.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getById(@PathVariable(value = "id") int userId) throws NotFoundException {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping("all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllWithPagination(pageable));
    }

    //    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody User user) throws DuplicateDataException, MessagingException {
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/all")
    public ResponseEntity<Void> saveAllUsers(@Valid @RequestBody List<User> users) {
        userService.saveAll(users);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) throws DuplicateDataException, NotFoundException, InternalErrorException {
        userService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("resend_password")
    public ResponseEntity<Void> resendPassword(@Valid @RequestBody ResetPasswordRequest request) throws DuplicateDataException, NotFoundException, InternalErrorException {
        userService.resendPassword(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("verify_user")
    public ResponseEntity<User> verifyUser(@Valid @RequestBody VerifyUserRequest request) throws NotFoundException, InternalErrorException {
        return ResponseEntity.ok(userService.verifyUser(request));
    }

    @PostMapping("resend_verification_code")
    public ResponseEntity<Void> resendVerificationCode(@Valid @RequestBody ResendVerificationCodeRequest request) throws NotFoundException, InternalErrorException {
        userService.resendVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> editUserInfo(@Valid @RequestBody UpdateUserRequest request) throws BadRequestException, NotFoundException {
        userService.update(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> removeUser(@PathVariable(value = "id") int userId) throws NotFoundException {
        userService.delete(userId);
        return ResponseEntity.ok().build();

    }
}

