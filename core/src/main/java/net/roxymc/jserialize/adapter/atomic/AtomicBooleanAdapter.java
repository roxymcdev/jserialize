package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicBooleanAdapter implements TypeAdapter.Mutable<AtomicBoolean> {
    @Override
    public @Nullable AtomicBoolean mutate(
            Reader reader, TypeRef<? extends AtomicBoolean> type, @Nullable AtomicBoolean ref, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicBoolean.class, type.getRawType());

        Boolean value = ctx.read(reader, boolean.class);

        if (value == null) {
            return ref;
        }

        if (ref == null) {
            ref = new AtomicBoolean();
        }

        ref.set(value);
        return ref;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicBoolean> type, @Nullable AtomicBoolean ref, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicBoolean.class, type.getRawType());

        Boolean value = ref != null ? ref.get() : null;

        ctx.write(writer, boolean.class, value);
    }
}
