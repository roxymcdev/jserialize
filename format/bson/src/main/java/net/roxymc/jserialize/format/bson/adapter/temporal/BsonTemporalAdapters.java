package net.roxymc.jserialize.format.bson.adapter.temporal;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.time.Instant;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.exactRaw;

public final class BsonTemporalAdapters {
    public static final TypeAdapter<Instant> INSTANT = new BsonInstantAdapter();

    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            exactRaw(Instant.class, INSTANT)
    );

    private BsonTemporalAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
