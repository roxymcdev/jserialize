package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

public final class TemporalAdapters {
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            DateAdapter.factory(),
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
