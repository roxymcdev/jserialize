package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.util.ObjectUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

abstract class AbstractMethodRef implements MethodRef {
    private final Type valueType;
    private final Class<?> declaringClass;
    final MethodHandle handle;

    AbstractMethodRef(Type valueType, Class<?> declaringClass, MethodHandle handle) {
        this.valueType = valueType;
        this.declaringClass = declaringClass;
        this.handle = handle;
    }

    @Override
    public Type valueType() {
        return valueType;
    }

    @Override
    public Class<?> declaringClass() {
        return declaringClass;
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this)
                .add("valueType=" + valueType)
                .add("declaringClass=" + declaringClass)
                .toString();
    }
}
