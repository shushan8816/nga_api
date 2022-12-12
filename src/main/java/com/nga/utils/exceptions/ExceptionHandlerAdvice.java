package com.nga.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(NotFoundException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<?> internalError(InternalErrorException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<?> duplicateDataException(DuplicateDataException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

       @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> authenticationException(JwtAuthenticationException e) {
        return new ResponseEntity<>(createMessage(e, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

       private Map<String, Object> createMessage(Throwable e, HttpStatus status) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();

        errorResponse.put("statusCode", status.value());
        errorResponse.put("timestamp", new Date());
        errorResponse.put("error", e.getMessage());

        return errorResponse;
    }
}
