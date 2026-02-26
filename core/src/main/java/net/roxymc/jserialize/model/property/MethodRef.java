package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;

@ApiStatus.NonExtendable
public interface MethodRef {
    Type valueType();

    Class<?> declaringClass();
}
