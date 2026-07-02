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

abstract class AbstractTemporalAdapter<T extends Temporal> implements TypeAdapter<T>, TemporalQuery<T> {
    private final TypeRef<T> type;
    private final DateTimeFormatter formatter;

    protected AbstractTemporalAdapter(TypeRef<T> type, DateTimeFormatter formatter) {
        this.type = nonNull(type, "type");
        this.formatter = nonNull(formatter, "formatter");
    }

    @Override
    public @Nullable T read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return formatter.parse(reader.readString(), this);
    }

    @Override
    public void write(Writer writer, @Nullable T value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(formatter.format(value));
    }

    @Override
    public TypeRef<? extends T> type() {
        return type;
    }
}
