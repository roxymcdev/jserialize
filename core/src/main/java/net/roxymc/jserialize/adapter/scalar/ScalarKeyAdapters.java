package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.KeyAdapter;

public final class ScalarKeyAdapters {
    private static final KeyAdapter.Factory FACTORY = KeyAdapter.Factory.composite(
            CharacterKeyAdapter.factory(),
            BooleanKeyAdapter.factory(),
            ByteKeyAdapter.factory(),
            DoubleKeyAdapter.factory(),
            FloatKeyAdapter.factory(),
            IntegerKeyAdapter.factory(),
            LongKeyAdapter.factory(),
            ShortKeyAdapter.factory(),
            NumberKeyAdapter.factory(),
            StringKeyAdapter.factory(),
            URIKeyAdapter.factory(),
            URLKeyAdapter.factory(),
            UUIDKeyAdapter.factory()
    );

    private ScalarKeyAdapters() {
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }
}
