package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalDateTimeAdapter extends AbstractTemporalAdapter<LocalDateTime> {
    private static final TypeRef<LocalDateTime> TYPE = TypeRef.of(LocalDateTime.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private LocalDateTimeAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new LocalDateTimeAdapter(formatter));
    }

    @Override
    public LocalDateTime queryFrom(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }
}
