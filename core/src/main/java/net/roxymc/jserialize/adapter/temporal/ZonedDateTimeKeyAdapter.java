package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.type.TypeRef;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class ZonedDateTimeKeyAdapter extends AbstractTemporalKeyAdapter<ZonedDateTime> {
    private static final TypeRef<ZonedDateTime> TYPE = TypeRef.of(ZonedDateTime.class);
    private static final Factory FACTORY = factory(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    private ZonedDateTimeKeyAdapter(DateTimeFormatter formatter) {
        super(TYPE, formatter);
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(DateTimeFormatter formatter) {
        return Factory.exact(new ZonedDateTimeKeyAdapter(formatter));
    }

    @Override
    public ZonedDateTime queryFrom(TemporalAccessor temporal) {
        return ZonedDateTime.from(temporal);
    }
}
