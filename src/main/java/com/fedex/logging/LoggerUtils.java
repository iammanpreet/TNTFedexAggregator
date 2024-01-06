package com.fedex.logging;

import org.slf4j.Logger;

public class LoggerUtils {

    public static void logInfo(Logger logger, String message, Object... args) {
        if (logger.isInfoEnabled()) {
            String formattedMessage = String.format(message, args);
            logger.info(formattedMessage);
        }
    }

    public static void logError(Logger logger, String message, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(message, args);
        }
    }

    public static void logError(Logger logger, String message, Throwable throwable, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(message, throwable, args);
        }
    }

}