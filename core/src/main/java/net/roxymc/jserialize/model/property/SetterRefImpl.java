package net.roxymc.jserialize.model.property;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

final class SetterRefImpl extends AbstractMethodRef implements SetterRef {
    SetterRefImpl(Type valueType, Class<?> declaringClass, MethodHandle handle) {
        super(valueType, declaringClass, handle);
    }

    @Override
    public void set(Object instance, @Nullable Object value) throws Throwable {
        handle.invoke(instance, value);
    }
}
