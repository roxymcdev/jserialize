package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class BooleanKeyAdapter extends AbstractKeyAdapter<Boolean> {
    private static final TypeRef<Boolean> TYPE = TypeRef.of(Boolean.class);

    public static final KeyAdapter<Boolean> INSTANCE = new BooleanKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private BooleanKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public TypeRef<? extends Boolean> type() {
        return TYPE;
    }
}
