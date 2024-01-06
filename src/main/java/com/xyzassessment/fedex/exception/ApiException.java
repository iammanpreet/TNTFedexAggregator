package com.xyzassessment.fedex.exception;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 3982091027975081466L;

    public ApiException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
