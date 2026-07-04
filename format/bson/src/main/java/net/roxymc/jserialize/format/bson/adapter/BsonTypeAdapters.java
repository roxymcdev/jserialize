package net.roxymc.jserialize.format.bson.adapter;

import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.format.bson.adapter.scalar.BsonUUIDAdapter;
import net.roxymc.jserialize.format.bson.adapter.temporal.BsonInstantAdapter;

public final class BsonTypeAdapters {
    public static final TypeAdapters DEFAULT = TypeAdapters.builder()
            .add(BsonUUIDAdapter.factory())
            .add(BsonInstantAdapter.factory())
            .addAll(TypeAdapters.DEFAULT)
            .build();

    private BsonTypeAdapters() {
    }
}
