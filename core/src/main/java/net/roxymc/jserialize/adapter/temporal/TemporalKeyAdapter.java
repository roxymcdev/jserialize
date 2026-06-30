package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;
import org.jspecify.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class TemporalKeyAdapter<T extends Temporal> implements KeyAdapter<T> {
    private final DateTimeFormatter formatter;
    private final TemporalQuery<T> query;

    TemporalKeyAdapter(DateTimeFormatter formatter, TemporalQuery<T> query) {
        this.formatter = nonNull(formatter, "formatter");
        this.query = nonNull(query, "query");
    }

    @Override
    public @Nullable T decode(@Nullable String value) {
        return value != null ? formatter.parse(value, query) : null;
    }

    @Override
    public String encode(@Nullable T value) {
        return formatter.format(nonNull(value, "value"));
    }
}
