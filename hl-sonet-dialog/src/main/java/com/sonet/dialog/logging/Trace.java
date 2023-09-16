package com.sonet.dialog.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {

    String[] ignorableParams() default {};

    String inputDescription() default "";

    String outputDescription() default "";
}
