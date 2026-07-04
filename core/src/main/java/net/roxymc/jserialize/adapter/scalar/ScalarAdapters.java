package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.TypeAdapter;

public final class ScalarAdapters {
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            CharacterAdapter.factory(),
            BooleanAdapter.factory(),
            ByteAdapter.factory(),
            DoubleAdapter.factory(),
            FloatAdapter.factory(),
            IntegerAdapter.factory(),
            LongAdapter.factory(),
            ShortAdapter.factory(),
            NumberAdapter.factory(),
            StringAdapter.factory(),
            EnumAdapter.factory(),
            PathAdapter.factory(),
            FileAdapter.factory(),
            URIAdapter.factory(),
            URLAdapter.factory(),
            UUIDAdapter.factory()
    );

    private ScalarAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
