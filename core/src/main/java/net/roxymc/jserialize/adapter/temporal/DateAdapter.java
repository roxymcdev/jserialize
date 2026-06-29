package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class DateAdapter implements TypeAdapter<Date> {
    @Override
    public @Nullable Date read(Reader reader, TypeRef<? extends Date> type, ReadContext ctx) throws IOException {
        checkAssignable(Date.class, type.getRawType());

        Instant instant = ctx.read(reader, Instant.class);
        return instant != null ? Date.from(instant) : null;
    }

    @Override
    public void write(Writer writer, TypeRef<? extends Date> type, @Nullable Date value, WriteContext ctx) throws IOException {
        checkAssignable(Date.class, type.getRawType());

        Instant instant = value != null ? value.toInstant() : null;
        ctx.write(writer, Instant.class, instant);
    }
}
