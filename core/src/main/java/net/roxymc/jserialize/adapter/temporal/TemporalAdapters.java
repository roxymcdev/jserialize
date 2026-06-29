package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.util.Date;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.exactRaw;

public final class TemporalAdapters {
    public static final TypeAdapter<Date> DATE = new DateAdapter();

    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            exactRaw(Date.class, DATE),
            InstantAdapter.factory(),
            LocalDateAdapter.factory(),
            LocalDateTimeAdapter.factory(),
            LocalTimeAdapter.factory(),
            OffsetDateTimeAdapter.factory(),
            ZonedDateTimeAdapter.factory()
    );

    private TemporalAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
