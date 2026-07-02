package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;

public final class TemporalKeyAdapters {
    private static final KeyAdapter.Factory FACTORY = KeyAdapter.Factory.composite(
            DateKeyAdapter.factory(),
            InstantKeyAdapter.factory(),
            LocalDateKeyAdapter.factory(),
            LocalTimeKeyAdapter.factory(),
            LocalDateTimeKeyAdapter.factory(),
            OffsetDateTimeKeyAdapter.factory(),
            ZonedDateTimeKeyAdapter.factory()
    );

    private TemporalKeyAdapters() {
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }
}
