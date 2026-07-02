package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class StringKeyAdapter extends AbstractKeyAdapter<String> {
    private static final TypeRef<String> TYPE = TypeRef.of(String.class);

    public static final KeyAdapter<String> INSTANCE = new StringKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private StringKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected String parse(String value) {
        return value;
    }

    @Override
    public TypeRef<? extends String> type() {
        return TYPE;
    }
}
