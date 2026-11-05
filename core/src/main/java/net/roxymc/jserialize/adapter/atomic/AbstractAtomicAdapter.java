package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedParameterizedType;
import java.util.concurrent.atomic.AtomicReference;

import static net.roxymc.jserialize.util.TypeUtils.resolveTypeParams;

abstract class AbstractAtomicAdapter<A, V extends @Nullable Object> implements TypeAdapter.Mutable<A> {
    private final TypeRef<@Nullable A> atomicType;
    private final TypeRef<V> valueType;

    protected AbstractAtomicAdapter(Class<A> atomicType, Class<V> valueType) {
        this.atomicType = TypeRef.of(atomicType);
        this.valueType = TypeRef.of(valueType);
    }

    protected AbstractAtomicAdapter(TypeRef<@Nullable A> atomicType) {
        AnnotatedParameterizedType ptype = resolveTypeParams(atomicType, AtomicReference.class);

        this.atomicType = atomicType;
        this.valueType = TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
    }

    protected abstract A createAtomic();

    protected abstract V getAtomic(A atomic);

    protected abstract void setAtomic(A atomic, @Nullable V value);

    @Override
    public @Nullable A mutate(Reader reader, @Nullable A atomic, ReadContext ctx) throws IOException {
        TypeAdapter<@NonNull V> valueAdapter = ctx.typeAdapters().getOrThrow(valueType);

        V value;

        if (valueAdapter instanceof TypeAdapter.Mutable && reader.peek() != TokenTypes.NULL) {
            V oldValue = atomic != null ? getAtomic(atomic) : null;

            value = ((TypeAdapter.Mutable<@NonNull V>) valueAdapter).mutate(reader, oldValue, ctx);
        } else {
            value = valueAdapter.read(reader, ctx);
        }

        if (atomic == null && value != null) {
            atomic = createAtomic();
        }

        if (atomic != null) {
            setAtomic(atomic, value);
        }

        return atomic;
    }

    @Override
    public void write(Writer writer, @Nullable A atomic, WriteContext ctx) throws IOException {
        V value = atomic != null ? getAtomic(atomic) : null;

        ctx.write(writer, valueType, value);
    }

    @Override
    public TypeRef<? extends @Nullable A> type() {
        return atomicType;
    }
}
