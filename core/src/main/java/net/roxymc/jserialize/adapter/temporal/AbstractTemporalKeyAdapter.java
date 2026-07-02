package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

abstract class AbstractTemporalKeyAdapter<T extends Temporal> implements KeyAdapter<T>, TemporalQuery<T> {
    private final TypeRef<T> type;
    private final DateTimeFormatter formatter;

    protected AbstractTemporalKeyAdapter(TypeRef<T> type, DateTimeFormatter formatter) {
        this.type = nonNull(type, "type");
        this.formatter = nonNull(formatter, "formatter");
    }

    @Override
    public @Nullable T decode(@Nullable String value) {
        return value != null ? formatter.parse(value, this) : null;
    }

    @Override
    public String encode(@Nullable T value) {
        return formatter.format(nonNull(value, "value"));
    }

    @Override
    public TypeRef<? extends T> type() {
        return type;
    }
}
