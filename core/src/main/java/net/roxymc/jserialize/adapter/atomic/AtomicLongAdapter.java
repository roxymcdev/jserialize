package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicLongAdapter implements TypeAdapter.Mutable<AtomicLong> {
    @Override
    public @Nullable AtomicLong mutate(
            Reader reader, TypeRef<? extends AtomicLong> type, @Nullable AtomicLong value, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicLong.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return value;
        }

        if (value == null) {
            value = new AtomicLong();
        }

        value.set(reader.readLong());
        return value;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicLong> type, @Nullable AtomicLong value, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicLong.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeLong(value.get());
    }
}
