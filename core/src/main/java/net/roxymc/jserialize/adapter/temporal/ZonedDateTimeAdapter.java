package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class ZonedDateTimeAdapter extends AbstractTemporalAdapter<ZonedDateTime> {
    private static final TypeAdapter.Factory FACTORY = factory(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    public ZonedDateTimeAdapter(DateTimeFormatter formatter) {
        super(ZonedDateTime.class, formatter);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(DateTimeFormatter formatter) {
        return TypeAdapter.Factory.exactRaw(ZonedDateTime.class, new ZonedDateTimeAdapter(formatter));
    }

    @Override
    protected ZonedDateTime from(TemporalAccessor temporal) {
        return ZonedDateTime.from(temporal);
    }
}
