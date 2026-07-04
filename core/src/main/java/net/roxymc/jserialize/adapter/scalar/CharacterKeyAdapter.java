package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class CharacterKeyAdapter extends AbstractKeyAdapter<Character> {
    private static final TypeRef<Character> TYPE = TypeRef.of(Character.class);

    public static final KeyAdapter<Character> INSTANCE = new CharacterKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private CharacterKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Character parse(String value) {
        if (value.length() != 1) {
            throw new IllegalStateException("value must be a single character");
        }

        return value.charAt(0);
    }

    @Override
    public TypeRef<? extends Character> type() {
        return TYPE;
    }
}
