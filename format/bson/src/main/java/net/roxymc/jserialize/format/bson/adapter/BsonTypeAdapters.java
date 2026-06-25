package net.roxymc.jserialize.format.bson.adapter;

import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.format.bson.adapter.scalar.BsonUUIDAdapter;

public final class BsonTypeAdapters {
    public static final TypeAdapters DEFAULT = TypeAdapters.builder()
            .add(BsonUUIDAdapter.factory())
            .addAll(TypeAdapters.DEFAULT)
            .build();

    private BsonTypeAdapters() {
    }
}
