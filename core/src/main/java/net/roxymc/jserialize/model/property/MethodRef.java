package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.AnnotatedType;

@ApiStatus.NonExtendable
public interface MethodRef {
    AnnotatedType valueType();

    Class<?> declaringClass();
}
