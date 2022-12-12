package com.nga.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateDataException extends Exception{

    private HttpStatus httpStatus;

    public DuplicateDataException() {
    }

    public DuplicateDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public DuplicateDataException(String message) {
        super(message);
    }
}
