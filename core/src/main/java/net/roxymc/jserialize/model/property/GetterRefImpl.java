package net.roxymc.jserialize.model.property;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

final class GetterRefImpl extends AbstractMethodRef implements GetterRef {
    GetterRefImpl(Type valueType, Class<?> declaringClass, MethodHandle handle) {
        super(valueType, declaringClass, handle);
    }

    @Override
    public @Nullable Object get(Object instance) throws Throwable {
        return handle.invoke(instance);
    }
}
