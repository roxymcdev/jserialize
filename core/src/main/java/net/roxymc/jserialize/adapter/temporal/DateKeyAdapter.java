package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class DateKeyAdapter implements KeyAdapter<Date> {
    static final KeyAdapter.Factory FACTORY = new Factory() {
        @Override
        public @Nullable <T> KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
            if (!Date.class.isAssignableFrom(type.getRawType())) {
                return null;
            }

            @SuppressWarnings("unchecked")
            KeyAdapter<T> adapter = (KeyAdapter<T>) new DateKeyAdapter(adapters.getKeyOrThrow(Instant.class));
            return adapter;
        }
    };

    private final KeyAdapter<Instant> instantAdapter;

    private DateKeyAdapter(KeyAdapter<Instant> instantAdapter) {
        this.instantAdapter = nonNull(instantAdapter, "instantAdapter");
    }

    @Override
    public @Nullable Date decode(@Nullable String value) {
        Instant instant = instantAdapter.decode(value);
        return instant != null ? Date.from(instant) : null;
    }

    @Override
    public String encode(@Nullable Date value) {
        Instant instant = value != null ? value.toInstant() : null;
        return instantAdapter.encode(instant);
    }
}
