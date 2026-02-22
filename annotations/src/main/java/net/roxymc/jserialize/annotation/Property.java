package net.roxymc.jserialize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Property {
    String value() default "";

    /**
     * works only on getter/parameter
     */
    boolean required() default false;

    /**
     * works only on getter
     */
    boolean writeNull() default false;

    /**
     * works only on getter
     */
    boolean mutate() default false;
}
