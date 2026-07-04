package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class InstantAdapter extends AbstractTemporalAdapter<Instant> {
    private static final TypeRef<Instant> TYPE = TypeRef.of(Instant.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_INSTANT);

    private InstantAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new InstantAdapter(formatter));
    }

    @Override
    public Instant queryFrom(TemporalAccessor temporal) {
        return Instant.from(temporal);
    }
}
