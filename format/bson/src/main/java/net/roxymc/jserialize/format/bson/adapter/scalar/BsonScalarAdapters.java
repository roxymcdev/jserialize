package net.roxymc.jserialize.format.bson.adapter.scalar;

import net.roxymc.jserialize.adapter.TypeAdapter;

public final class BsonScalarAdapters {
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            BsonUUIDAdapter.factory()
    );

    private BsonScalarAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
