package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class TemporalAdapter<T extends Temporal> implements TypeAdapter<T> {
    private final Class<T> type;
    private final DateTimeFormatter formatter;
    private final TemporalQuery<T> query;

    TemporalAdapter(Class<T> type, DateTimeFormatter formatter, TemporalQuery<T> query) {
        this.type = nonNull(type, "type");
        this.formatter = nonNull(formatter, "formatter");
        this.query = nonNull(query, "query");
    }

    @Override
    public @Nullable T read(Reader reader, TypeRef<? extends T> typeRef, ReadContext ctx) throws IOException {
        checkAssignable(type, typeRef.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return formatter.parse(reader.readString(), query);
    }

    @Override
    public void write(Writer writer, TypeRef<? extends T> typeRef, @Nullable T value, WriteContext ctx) throws IOException {
        checkAssignable(type, typeRef.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(formatter.format(value));
    }
}
