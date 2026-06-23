package net.roxymc.jserialize.type;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.util.VarHandles;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeRef<T extends @UnknownNullability Object> {
    private static final VarHandle RAW_TYPE_HANDLE = VarHandles.find(TypeRef.class, "rawType", Class.class);

    private final AnnotatedType annotatedType;
    private @Nullable Class<? super T> rawType;

    protected TypeRef() {
        this.annotatedType = extractType();
    }

    private TypeRef(AnnotatedType type) {
        this.annotatedType = type;
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

    @SuppressWarnings("unchecked")
    public final Class<? super T> getRawType() {
        Class<? super T> value = (Class<? super T>) RAW_TYPE_HANDLE.getAcquire(this);

        if (value == null) {
            Class<? super T> erased = (Class<? super T>) GenericTypeReflector.erase(getType());
            Class<? super T> witness = (Class<? super T>) RAW_TYPE_HANDLE.compareAndExchangeRelease(this, null, erased);

            value = witness != null ? witness : erased;
        }

        return value;
    }

    private AnnotatedType extractType() {
        AnnotatedType type = getClass().getAnnotatedSuperclass();
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException("missing type parameters");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;
        if (((ParameterizedType) ptype.getType()).getRawType() != TypeRef.class) {
            throw new IllegalStateException("does not directly extend TypeToken");
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
        return "TypeToken[" + getType() + "]";
    }
}
