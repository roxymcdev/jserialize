package net.roxymc.jserialize.type;

import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public abstract class TypeRef<T extends @UnknownNullability Object> {
    private final AnnotatedType annotatedType;
    private final Class<? super T> rawType;

    @SuppressWarnings("unchecked")
    protected TypeRef() {
        this.annotatedType = GenericTypeReflector.toCanonical(extractType());
        this.rawType = (Class<? super T>) GenericTypeReflector.erase(getType());
    }

    @SuppressWarnings("unchecked")
    private TypeRef(AnnotatedType type) {
        this.annotatedType = GenericTypeReflector.toCanonical(nonNull(type, "type"));
        this.rawType = (Class<? super T>) GenericTypeReflector.erase(getType());
    }

    public static <T extends @UnknownNullability Object> TypeRef<T> of(Class<T> type) {
        return of((Type) type);
    }

    public static <T extends @UnknownNullability Object> TypeRef<T> of(Type type) {
        return of(GenericTypeReflector.annotate(type));
    }

    public static <T extends @UnknownNullability Object> TypeRef<T> of(AnnotatedType type) {
        return new TypeRef<>(type) {
        };
    }

    public final Type getType() {
        return annotatedType.getType();
    }

    public final AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    public final Class<? super T> getRawType() {
        return rawType;
    }

    private AnnotatedType extractType() {
        AnnotatedType type = getClass().getAnnotatedSuperclass();

        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException("missing type parameters");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;
        if (((ParameterizedType) ptype.getType()).getRawType() != TypeRef.class) {
            throw new IllegalStateException("does not directly extend TypeRef");
        }

        return ptype.getAnnotatedActualTypeArguments()[0];
    }

    @Override
    public final int hashCode() {
        return annotatedType.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof TypeRef)) {
            return false;
        }

        TypeRef<?> that = (TypeRef<?>) obj;
        return this.annotatedType.equals(that.annotatedType);
    }

    @Override
    public final String toString() {
        return "TypeRef[" + getType() + "]";
    }
}
