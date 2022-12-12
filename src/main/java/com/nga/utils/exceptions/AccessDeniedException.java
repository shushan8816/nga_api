package com.nga.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends Exception {

    private HttpStatus httpStatus;

    public AccessDeniedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
