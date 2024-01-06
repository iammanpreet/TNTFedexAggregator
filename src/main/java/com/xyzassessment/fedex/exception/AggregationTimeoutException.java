package com.xyzassessment.fedex.exception;

public class AggregationTimeoutException extends RuntimeException {

    public AggregationTimeoutException(String message) {
        super(message);
    }

    public AggregationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}