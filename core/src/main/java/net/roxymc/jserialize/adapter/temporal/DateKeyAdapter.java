package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class DateKeyAdapter implements KeyAdapter<Date> {
    private static final TypeRef<Date> TYPE = TypeRef.of(Date.class);

    private static final Factory FACTORY = Factory.exactRaw(Date.class, ($, adapters) ->
            new DateKeyAdapter(adapters.getKeyOrThrow(Instant.class))
    );

    private final KeyAdapter<Instant> instantAdapter;

    private DateKeyAdapter(KeyAdapter<Instant> instantAdapter) {
        this.instantAdapter = nonNull(instantAdapter, "instantAdapter");
    }

    public static Factory factory() {
        return FACTORY;
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

    @Override
    public TypeRef<? extends Date> type() {
        return TYPE;
    }
}
