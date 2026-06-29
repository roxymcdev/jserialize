package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalDateTimeAdapter extends AbstractTemporalAdapter<LocalDateTime> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    public LocalDateTimeAdapter(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(LocalDateTime.class, new LocalDateTimeAdapter(formatter));
    }

    @Override
    protected LocalDateTime from(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }
}
