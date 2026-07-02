package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class OffsetDateTimeKeyAdapter extends AbstractTemporalKeyAdapter<OffsetDateTime> {
    private static final TypeRef<OffsetDateTime> TYPE = TypeRef.of(OffsetDateTime.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    private OffsetDateTimeKeyAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new OffsetDateTimeKeyAdapter(formatter));
    }

    @Override
    public OffsetDateTime queryFrom(TemporalAccessor temporal) {
        return OffsetDateTime.from(temporal);
    }
}
