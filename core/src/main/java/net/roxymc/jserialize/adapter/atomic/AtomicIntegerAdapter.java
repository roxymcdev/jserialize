package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicIntegerAdapter implements TypeAdapter.Mutable<AtomicInteger> {
    @Override
    public AtomicInteger mutate(
            Reader reader, TypeRef<? extends AtomicInteger> type, @Nullable AtomicInteger ref, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicInteger.class, type.getRawType());

        Integer value = ctx.read(reader, int.class);

        if (value == null) {
            throw new IllegalStateException("Cannot cannot set AtomicInteger to null");
        }

        if (ref == null) {
            ref = new AtomicInteger();
        }

        ref.set(value);
        return ref;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicInteger> type, @Nullable AtomicInteger ref, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicInteger.class, type.getRawType());

        Integer value = ref != null ? ref.get() : null;

        ctx.write(writer, int.class, value);
    }
}
