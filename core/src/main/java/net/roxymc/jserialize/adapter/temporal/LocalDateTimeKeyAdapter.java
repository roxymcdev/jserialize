package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalDateTimeKeyAdapter extends AbstractTemporalKeyAdapter<LocalDateTime> {
    private static final TypeRef<LocalDateTime> TYPE = TypeRef.of(LocalDateTime.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private LocalDateTimeKeyAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new LocalDateTimeKeyAdapter(formatter));
    }

    @Override
    public LocalDateTime queryFrom(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }
}
