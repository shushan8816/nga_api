package com.nga.services.interfaces;


import com.nga.dto.request.*;
import com.nga.enums.UserStatus;
import com.nga.models.User;
import com.nga.utils.exceptions.BadRequestException;
import com.nga.utils.exceptions.DuplicateDataException;
import com.nga.utils.exceptions.InternalErrorException;
import com.nga.utils.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User getById(int id) throws NotFoundException;

    User findByEmail(String email, UserStatus status);

    List<User> getAll();

    Page<User> getAllWithPagination(Pageable pageable);

    void save(User user) throws DuplicateDataException, MessagingException;

    void saveAll(List<User> userList);

    void update(UpdateUserRequest request) throws BadRequestException, NotFoundException;

    void delete(int id) throws NotFoundException;

    boolean existsByVerificationCode(String verificationCode);

    void resendPassword(ResetPasswordRequest request) throws NotFoundException, DuplicateDataException, InternalErrorException;

    void forgotPassword(ForgotPasswordRequest request) throws NotFoundException, DuplicateDataException, InternalErrorException;

    User verifyUser(VerifyUserRequest request) throws NotFoundException, InternalErrorException;

    void resendVerificationCode(ResendVerificationCodeRequest request) throws NotFoundException, InternalErrorException;

}
