package com.nga.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends Exception {

    public InternalErrorException() {
    }

    public InternalErrorException(String message) {
        super(message);
    }
}
