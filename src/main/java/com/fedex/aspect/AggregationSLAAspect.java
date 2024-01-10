package com.fedex.aspect;

import com.fedex.annotations.SlaEnforcement;
import com.fedex.exception.AggregationTimeoutException;
import com.fedex.logging.LoggerUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AggregationSLAAspect {

    private static final Logger logger = LoggerFactory.getLogger(AggregationSLAAspect.class);
    /**
     * This method defines the implementation to be executed for the method which is wrapped under SlaEnforcement annotation
     * */
    @Around("@annotation(slaEnforcement)")
    public Object enforceSLA(ProceedingJoinPoint joinPoint, SlaEnforcement slaEnforcement) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logExecutionTime(joinPoint, executionTime);
            enforceSLA(executionTime, slaEnforcement.threshold());
            return result;
        } catch (Exception e) {
            logExecutionTime(joinPoint, -1); // Log the error related to SLA breach
            throw e;
        }
    }

    /**
     * This method checks if the SLA is breached or not
     * */
    private void enforceSLA(long executionTime, long slaThreshold) {
        if (executionTime > slaThreshold) {
            LoggerUtils.logError(logger, "SLA violation! Execution time: {} ms", executionTime);
            throw new AggregationTimeoutException("Aggregation process exceeded SLA");
        }
    }

    /**
     * This method is used to log the total execution time for the request
     * */
    private void logExecutionTime(ProceedingJoinPoint joinPoint, long executionTime) {
        LoggerUtils.logInfo(logger, "{} executed in {} ms", joinPoint.getSignature(), executionTime);
    }
}