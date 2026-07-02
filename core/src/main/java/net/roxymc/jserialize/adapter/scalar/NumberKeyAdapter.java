package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class NumberKeyAdapter extends AbstractKeyAdapter<Number> {
    private static final TypeRef<Number> TYPE = TypeRef.of(Number.class);

    public static final KeyAdapter<Number> INSTANCE = new NumberKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private NumberKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Number parse(String value) {
        return NumberAdapter.parseNumber(value);
    }

    @Override
    public TypeRef<? extends Number> type() {
        return TYPE;
    }
}
