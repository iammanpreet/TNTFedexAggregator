package com.xyzassessment.fedex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlaEnforcement {
    /**
     * Default SLA threshold is 10 sec
     */
    long threshold() default 10000;
}