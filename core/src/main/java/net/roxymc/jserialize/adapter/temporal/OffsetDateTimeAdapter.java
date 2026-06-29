package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class OffsetDateTimeAdapter extends AbstractTemporalAdapter<OffsetDateTime> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    public OffsetDateTimeAdapter(DateTimeFormatter formatter) {
        super(OffsetDateTime.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(OffsetDateTime.class, new OffsetDateTimeAdapter(formatter));
    }

    @Override
    protected OffsetDateTime from(TemporalAccessor temporal) {
        return OffsetDateTime.from(temporal);
    }
}
