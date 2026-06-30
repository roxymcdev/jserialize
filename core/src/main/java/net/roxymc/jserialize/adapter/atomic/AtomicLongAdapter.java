package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicLongAdapter implements TypeAdapter.Mutable<AtomicLong> {
    @Override
    public AtomicLong mutate(
            Reader reader, TypeRef<? extends AtomicLong> type, @Nullable AtomicLong ref, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicLong.class, type.getRawType());

        Long value = ctx.read(reader, long.class);

        if (value == null) {
            throw new IllegalStateException("Cannot cannot set AtomicLong to null");
        }

        if (ref == null) {
            ref = new AtomicLong();
        }

        ref.set(value);
        return ref;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicLong> type, @Nullable AtomicLong ref, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicLong.class, type.getRawType());

        Long value = ref != null ? ref.get() : null;

        ctx.write(writer, long.class, value);
    }
}
