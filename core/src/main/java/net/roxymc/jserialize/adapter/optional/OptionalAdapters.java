package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.adapter.TypeAdapter;

public final class OptionalAdapters {
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            OptionalAdapter.factory(),
            OptionalDoubleAdapter.factory(),
            OptionalIntAdapter.factory(),
            OptionalLongAdapter.factory()
    );

    private OptionalAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
