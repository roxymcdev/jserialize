package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalTimeAdapter extends AbstractTemporalAdapter<LocalTime> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_TIME);

    public LocalTimeAdapter(DateTimeFormatter formatter) {
        super(LocalTime.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(LocalTime.class, new LocalTimeAdapter(formatter));
    }

    @Override
    protected LocalTime from(TemporalAccessor temporal) {
        return LocalTime.from(temporal);
    }
}
