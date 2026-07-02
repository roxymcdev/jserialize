package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class LongKeyAdapter extends AbstractKeyAdapter<Long> {
    private static final TypeRef<Long> TYPE = TypeRef.of(Long.class);

    public static final KeyAdapter<Long> INSTANCE = new LongKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private LongKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Long parse(String value) {
        return Long.parseLong(value);
    }

    @Override
    public TypeRef<? extends Long> type() {
        return TYPE;
    }
}
