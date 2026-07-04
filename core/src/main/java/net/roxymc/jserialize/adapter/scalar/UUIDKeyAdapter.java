package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

import java.util.UUID;

public final class UUIDKeyAdapter extends AbstractKeyAdapter<UUID> {
    private static final TypeRef<UUID> TYPE = TypeRef.of(UUID.class);

    public static final KeyAdapter<UUID> INSTANCE = new UUIDKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private UUIDKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected UUID parse(String value) {
        return UUID.fromString(value);
    }

    @Override
    public TypeRef<? extends UUID> type() {
        return TYPE;
    }
}
