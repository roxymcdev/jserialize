package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalDateAdapter extends AbstractTemporalAdapter<LocalDate> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_DATE);

    public LocalDateAdapter(DateTimeFormatter formatter) {
        super(LocalDate.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(LocalDate.class, new LocalDateAdapter(formatter));
    }

    @Override
    protected LocalDate from(TemporalAccessor accessor) {
        return LocalDate.from(accessor);
    }
}
