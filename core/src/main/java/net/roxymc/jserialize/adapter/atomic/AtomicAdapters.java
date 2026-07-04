package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.adapter.TypeAdapter;

public final class AtomicAdapters {
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            AtomicBooleanAdapter.factory(),
            AtomicIntegerAdapter.factory(),
            AtomicLongAdapter.factory(),
            AtomicReferenceAdapter.factory()
    );

    private AtomicAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
