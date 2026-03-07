package net.roxymc.jserialize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSerializable {
    FieldResolution fields() default FieldResolution.ALWAYS;

    MethodResolution methods() default MethodResolution.BEAN;

    boolean mutateOnly() default false;

    enum FieldResolution {
        ALWAYS,
        ANNOTATED_ONLY,
        NEVER
    }

    enum MethodResolution {
        ALWAYS,
        BEAN,
        ANNOTATED_ONLY,
        NEVER
    }
}
