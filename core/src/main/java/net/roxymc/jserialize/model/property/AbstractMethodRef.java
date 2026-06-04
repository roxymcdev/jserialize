package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.util.ObjectUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedType;

abstract class AbstractMethodRef implements MethodRef {
    private final AnnotatedType valueType;
    private final Class<?> declaringClass;
    final MethodHandle handle;

    AbstractMethodRef(AnnotatedType valueType, Class<?> declaringClass, MethodHandle handle) {
        this.valueType = valueType;
        this.declaringClass = declaringClass;
        this.handle = handle;
    }

    @Override
    public AnnotatedType valueType() {
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
