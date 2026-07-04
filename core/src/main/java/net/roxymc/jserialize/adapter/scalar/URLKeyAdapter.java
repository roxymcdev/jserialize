package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public final class URLKeyAdapter extends AbstractKeyAdapter<URL> {
    private static final TypeRef<URL> TYPE = TypeRef.of(URL.class);

    public static final KeyAdapter<URL> INSTANCE = new URLKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private URLKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected URL parse(String value) {
        try {
            return URI.create(value).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + value, e);
        }
    }

    @Override
    public TypeRef<? extends URL> type() {
        return TYPE;
    }
}
