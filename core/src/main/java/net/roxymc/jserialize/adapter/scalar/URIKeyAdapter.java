package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

import java.net.URI;

public final class URIKeyAdapter extends AbstractKeyAdapter<URI> {
    private static final TypeRef<URI> TYPE = TypeRef.of(URI.class);

    public static final KeyAdapter<URI> INSTANCE = new URIKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private URIKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected URI parse(String value) {
        return URI.create(value);
    }

    @Override
    public TypeRef<? extends URI> type() {
        return TYPE;
    }
}
