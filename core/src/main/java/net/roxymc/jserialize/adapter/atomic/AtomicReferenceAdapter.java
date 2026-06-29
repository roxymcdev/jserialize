package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.concurrent.atomic.AtomicReference;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.getExactSuperType;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicReferenceAdapter implements TypeAdapter.Mutable<AtomicReference<?>> {
    @Override
    public @Nullable AtomicReference<?> mutate(
            Reader reader, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<?> value, ReadContext ctx
    ) throws IOException {
        return mutate0(reader, type, value, ctx);
    }

    private <V extends @Nullable Object> @Nullable AtomicReference<V> mutate0(
            Reader reader, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<V> ref, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicReference.class, type.getRawType());

        TypeRef<@UnknownNullability V> valueType = resolveValueType(type);
        TypeAdapter<@NonNull V> valueAdapter = ctx.typeAdapters().getOrThrow(valueType);

        V value;

        if (valueAdapter instanceof TypeAdapter.Mutable) {
            V oldValue = ref != null ? ref.get() : null;

            value = ((TypeAdapter.Mutable<@NonNull V>) valueAdapter).mutate(reader, valueType, oldValue, ctx);
        } else {
            value = valueAdapter.read(reader, valueType, ctx);
        }

        if (value == null) {
            return ref;
        }

        if (ref == null) {
            ref = new AtomicReference<>();
        }

        ref.set(value);
        return ref;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <V extends @Nullable Object> void write0(
            Writer writer, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<V> ref, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicReference.class, type.getRawType());

        TypeRef<@UnknownNullability V> valueType = resolveValueType(type);

        V value = ref != null ? ref.get() : null;

        ctx.write(writer, valueType, value);
    }

    private <V extends @UnknownNullability Object> TypeRef<V> resolveValueType(TypeRef<? extends AtomicReference<?>> refType) {
        AnnotatedType type = getExactSuperType(capture(refType.getAnnotatedType()), AtomicReference.class);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(refType.getType() + " must be a parameterized AtomicReference");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;

        return TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
    }
}
