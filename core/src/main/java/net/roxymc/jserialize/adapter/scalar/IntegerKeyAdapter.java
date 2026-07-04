package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class IntegerKeyAdapter extends AbstractKeyAdapter<Integer> {
    private static final TypeRef<Integer> TYPE = TypeRef.of(Integer.class);

    public static final KeyAdapter<Integer> INSTANCE = new IntegerKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private IntegerKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public TypeRef<? extends Integer> type() {
        return TYPE;
    }
}
