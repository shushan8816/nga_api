package com.nga.controllers;

import com.nga.dto.request.ChangePasswordRequest;
import com.nga.dto.request.UpdateUserRequest;
import com.nga.models.User;
import com.nga.services.interfaces.UserService;
import com.nga.utils.exceptions.BadRequestException;
import com.nga.utils.exceptions.DuplicateDataException;
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

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllWithPagination(pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> editUserInfo(@Valid @RequestBody UpdateUserRequest request) throws BadRequestException, NotFoundException {
        userService.update(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<User> verifyAndChangeUserPassword(@Valid @RequestBody ChangePasswordRequest request) throws NotFoundException {
        return ResponseEntity.ok(userService.verifyAndChangePassword(request));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> removeUser(@PathVariable(value = "id") int userId) throws NotFoundException {
        userService.delete(userId);
        return ResponseEntity.ok().build();

    }
}

