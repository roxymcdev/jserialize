package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class ShortKeyAdapter extends AbstractKeyAdapter<Short> {
    private static final TypeRef<Short> TYPE = TypeRef.of(Short.class);

    public static final KeyAdapter<Short> INSTANCE = new ShortKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private ShortKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Short parse(String value) {
        return Short.parseShort(value);
    }

    @Override
    public TypeRef<? extends Short> type() {
        return TYPE;
    }
}
