package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

public final class ByteKeyAdapter extends AbstractKeyAdapter<Byte> {
    private static final TypeRef<Byte> TYPE = TypeRef.of(Byte.class);

    public static final KeyAdapter<Byte> INSTANCE = new ByteKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private ByteKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Byte parse(String value) {
        return Byte.parseByte(value);
    }

    @Override
    public TypeRef<? extends @Nullable Byte> type() {
        return TYPE;
    }
}
