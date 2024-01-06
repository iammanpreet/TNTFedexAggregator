package com.xyzassessment.fedex.aspect;

import com.xyzassessment.fedex.annotations.SlaEnforcement;
import com.xyzassessment.fedex.exception.AggregationTimeoutException;
import com.xyzassessment.fedex.logging.LoggerUtils;
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

    private void enforceSLA(long executionTime, long slaThreshold) {
        if (executionTime > slaThreshold) {
            LoggerUtils.logError(logger, "SLA violation! Execution time: {} ms", executionTime);
            throw new AggregationTimeoutException("Aggregation process exceeded SLA");
        }
    }

    private void logExecutionTime(ProceedingJoinPoint joinPoint, long executionTime) {
        LoggerUtils.logInfo(logger, "{} executed in {} ms", joinPoint.getSignature(), executionTime);
    }
}