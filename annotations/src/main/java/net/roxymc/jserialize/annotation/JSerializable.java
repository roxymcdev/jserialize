package net.roxymc.jserialize.annotation;

import net.roxymc.jserialize.util.PropertyResolution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSerializable {
    PropertyResolution fields() default PropertyResolution.ALWAYS;

    PropertyResolution methods() default PropertyResolution.ALWAYS;

    boolean mutateOnly() default false;
}
