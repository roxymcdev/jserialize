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

public final class DateAdapter implements TypeAdapter<Date> {
    private static final TypeRef<Date> TYPE = TypeRef.of(Date.class);

    public static final TypeAdapter<Date> INSTANCE = new DateAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private DateAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Date read(Reader reader, ReadContext ctx) throws IOException {
        Instant instant = ctx.read(reader, Instant.class);
        return instant != null ? Date.from(instant) : null;
    }

    @Override
    public void write(Writer writer, @Nullable Date value, WriteContext ctx) throws IOException {
        Instant instant = value != null ? value.toInstant() : null;
        ctx.write(writer, Instant.class, instant);
    }

    @Override
    public TypeRef<? extends Date> type() {
        return TYPE;
    }
}
