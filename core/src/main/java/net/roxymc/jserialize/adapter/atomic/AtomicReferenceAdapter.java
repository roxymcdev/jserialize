package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.UnknownNullability;
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
            Reader reader, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<V> value, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicReference.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return value;
        }

        if (value == null) {
            value = new AtomicReference<>();
        }

        TypeRef<@UnknownNullability V> valueType = resolveValueType(type);

        value.set(ctx.read(reader, valueType));
        return value;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <V extends @Nullable Object> void write0(
            Writer writer, TypeRef<? extends AtomicReference<?>> type, @Nullable AtomicReference<V> value, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicReference.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        TypeRef<@UnknownNullability V> valueType = resolveValueType(type);

        ctx.write(writer, valueType, value.get());
    }

    private <V extends @UnknownNullability Object> TypeRef<V> resolveValueType(TypeRef<? extends AtomicReference<?>> referenceType) {
        AnnotatedType type = getExactSuperType(capture(referenceType.getAnnotatedType()), AtomicReference.class);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(referenceType.getType() + " must be a parameterized AtomicReference");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;

        return TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
    }
}
