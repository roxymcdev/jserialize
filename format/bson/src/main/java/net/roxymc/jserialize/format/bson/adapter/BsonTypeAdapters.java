package net.roxymc.jserialize.format.bson.adapter;

import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.format.bson.adapter.scalar.BsonScalarAdapters;
import net.roxymc.jserialize.format.bson.adapter.temporal.BsonTemporalAdapters;

public final class BsonTypeAdapters {
    public static final TypeAdapters DEFAULT = TypeAdapters.builder()
            .add(BsonScalarAdapters.factory())
            .add(BsonTemporalAdapters.factory())
            .addAll(TypeAdapters.DEFAULT)
            .build();

    private BsonTypeAdapters() {
    }
}
