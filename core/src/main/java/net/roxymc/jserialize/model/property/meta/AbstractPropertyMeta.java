package net.roxymc.jserialize.model.property.meta;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

abstract class AbstractPropertyMeta<A extends Annotation> implements PropertyMeta {
    final @Nullable A annotation;

    AbstractPropertyMeta(@Nullable A annotation) {
        this.annotation = annotation;
    }

    boolean get(Object2BooleanFunction<A> function, boolean defaultValue) {
        return annotation != null ? function.apply(annotation) : defaultValue;
    }

    @Override
    public abstract PropertyKind<A> kind();

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("required=" + required())
                .add("writeNull=" + writeNull())
                .add("mutate=" + mutate())
                .toString();
    }

    @FunctionalInterface
    interface Object2BooleanFunction<T> {
        boolean apply(T t);
    }
}
