package net.roxymc.jserialize.adapter.temporal;

import net.roxymc.jserialize.adapter.KeyAdapter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;

import static net.roxymc.jserialize.adapter.KeyAdapter.Factory.exactRaw;

public final class TemporalKeyAdapters {
    private static final KeyAdapter.Factory FACTORY = KeyAdapter.Factory.composite(
            DateKeyAdapter.FACTORY,
            factory(Instant.class, DateTimeFormatter.ISO_INSTANT, Instant::from),
            factory(LocalDate.class, DateTimeFormatter.ISO_LOCAL_DATE, LocalDate::from),
            factory(LocalTime.class, DateTimeFormatter.ISO_LOCAL_TIME, LocalTime::from),
            factory(LocalDateTime.class, DateTimeFormatter.ISO_LOCAL_DATE_TIME, LocalDateTime::from),
            factory(OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME, OffsetDateTime::from),
            factory(ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME, ZonedDateTime::from)
    );

    private TemporalKeyAdapters() {
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    public static <T extends Temporal> KeyAdapter.Factory factory(
            Class<T> type, DateTimeFormatter formatter, TemporalQuery<T> query
    ) {
        return exactRaw(type, new TemporalKeyAdapter<>(formatter, query));
    }
}
