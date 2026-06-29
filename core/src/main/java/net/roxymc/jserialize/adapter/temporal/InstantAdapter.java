package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class InstantAdapter extends AbstractTemporalAdapter<Instant> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_INSTANT);

    public InstantAdapter(DateTimeFormatter formatter) {
        super(Instant.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(Instant.class, new InstantAdapter(formatter));
    }

    @Override
    protected Instant from(TemporalAccessor temporal) {
        return Instant.from(temporal);
    }
}
