package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class LocalTimeKeyAdapter extends AbstractTemporalKeyAdapter<LocalTime> {
    private static final TypeRef<LocalTime> TYPE = TypeRef.of(LocalTime.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_LOCAL_TIME);

    private LocalTimeKeyAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new LocalTimeKeyAdapter(formatter));
    }

    @Override
    public LocalTime queryFrom(TemporalAccessor temporal) {
        return LocalTime.from(temporal);
    }
}
