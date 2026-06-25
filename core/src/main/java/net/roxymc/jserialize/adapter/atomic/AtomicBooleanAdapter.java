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
import java.util.concurrent.atomic.AtomicBoolean;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class AtomicBooleanAdapter implements TypeAdapter.Mutable<AtomicBoolean> {
    @Override
    public @Nullable AtomicBoolean mutate(
            Reader reader, TypeRef<? extends AtomicBoolean> type, @Nullable AtomicBoolean value, ReadContext ctx
    ) throws IOException {
        checkAssignable(AtomicBoolean.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return value;
        }

        if (value == null) {
            value = new AtomicBoolean();
        }

        value.set(reader.readBoolean());
        return value;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends AtomicBoolean> type, @Nullable AtomicBoolean value, WriteContext ctx
    ) throws IOException {
        checkAssignable(AtomicBoolean.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeBoolean(value.get());
    }
}
