package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicPrimitiveAdapter<A, V> implements TypeAdapter.Mutable<A> {
    private final Class<A> atomicType;
    private final Class<V> valueType;

    private final Supplier<A> supplier;
    private final Function<A, V> getter;
    private final BiConsumer<A, V> setter;

    AtomicPrimitiveAdapter(
            Class<A> atomicType,
            Class<V> valueType,
            Supplier<A> supplier,
            Function<A, V> getter,
            BiConsumer<A, V> setter
    ) {
        if (!valueType.isPrimitive()) {
            throw new IllegalArgumentException("valueType must be primitive");
        }

        this.atomicType = nonNull(atomicType, "atomicType");
        this.valueType = nonNull(valueType, "valueType");

        this.supplier = nonNull(supplier, "supplier");
        this.getter = nonNull(getter, "getter");
        this.setter = nonNull(setter, "setter");
    }

    @Override
    public A mutate(Reader reader, TypeRef<? extends A> type, @Nullable A ref, ReadContext ctx) throws IOException {
        checkAssignable(atomicType, type.getRawType());

        V value = ctx.read(reader, valueType);

        if (value == null) {
            throw new IllegalStateException("Cannot cannot set atomic " + valueType + " to null");
        }

        if (ref == null) {
            ref = supplier.get();
        }

        setter.accept(ref, value);
        return ref;
    }

    @Override
    public void write(Writer writer, TypeRef<? extends A> type, @Nullable A ref, WriteContext ctx) throws IOException {
        checkAssignable(atomicType, type.getRawType());

        V value = ref != null ? getter.apply(ref) : null;

        ctx.write(writer, valueType, value);
    }
}
