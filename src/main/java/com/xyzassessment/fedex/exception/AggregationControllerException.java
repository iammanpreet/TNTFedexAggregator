package com.xyzassessment.fedex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AggregationControllerException {
    @ExceptionHandler(AggregationTimeoutException.class)
    public ResponseEntity<Object> handleTimeoutException(AggregationTimeoutException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "AggregationTimeoutException");
        body.put("message", "Aggregation process timed out");

        return new ResponseEntity<>(body, HttpStatus.REQUEST_TIMEOUT);
    }
}
