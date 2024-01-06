package com.fedex.exception;

import com.fedex.logging.LoggerUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

public class GlobalException {
    public static void handleHttpStatusError(Logger logger, HttpStatus statusCode, String apiUrl) {
        LoggerUtils.logError(logger, "HTTP Status Code Exception for API {}: {}", apiUrl, statusCode);
    }

    public static void handleHttpClientError(Logger logger, HttpStatus statusCode, String apiUrl) {
        LoggerUtils.logError(logger, "HTTP Status Code Exception for API {}: {}", apiUrl, statusCode);
    }

    public static void handleApiException(Logger logger, String apiUrl, Exception e) {
        LoggerUtils.logError(logger, "Api Exception while calling API: {}", apiUrl, e);
    }

    public static void handleGenericException(Logger logger, String apiUrl, Exception e) {
        LoggerUtils.logError(logger, "Exception while calling API: {}", apiUrl, e);
    }

}
