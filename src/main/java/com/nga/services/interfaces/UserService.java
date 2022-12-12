package com.nga.services.interfaces;


import com.nga.dto.request.ChangePasswordRequest;
import com.nga.dto.request.UpdateUserRequest;
import com.nga.enums.UserStatus;
import com.nga.models.User;
import com.nga.utils.exceptions.BadRequestException;
import com.nga.utils.exceptions.DuplicateDataException;
import com.nga.utils.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User getById(int id) throws NotFoundException;

    User getByEmail(String email, UserStatus status);

    boolean existsByVerificationCode( String verificationCode);

    List<User> getAllUsers();

    Page<User> getAllWithPagination(Pageable pageable);

    void save(User user) throws DuplicateDataException, MessagingException;

    void saveAll(List<User> userList);

    void update(UpdateUserRequest request) throws BadRequestException, NotFoundException;

    void delete(int id) throws NotFoundException;

    User verifyAndChangePassword(ChangePasswordRequest request) throws NotFoundException;

}
