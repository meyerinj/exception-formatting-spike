package com.opes.errTest.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;

public class PhoneNotFoundExceptionHAL extends HalParentException {
    public PhoneNotFoundExceptionHAL(String message, HttpRequest request) {
        super(message, HttpStatus.CONFLICT);
        setRequestUri(request.getUri());
        setSelfTemplate(true);
    }
}
