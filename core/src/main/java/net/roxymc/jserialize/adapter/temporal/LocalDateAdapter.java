package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalDateAdapter extends AbstractTemporalAdapter<LocalDate> {
    private static final TypeRef<LocalDate> TYPE = TypeRef.of(LocalDate.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_DATE);

    private LocalDateAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new LocalDateAdapter(formatter));
    }

    @Override
    public LocalDate queryFrom(TemporalAccessor temporal) {
        return LocalDate.from(temporal);
    }
}
