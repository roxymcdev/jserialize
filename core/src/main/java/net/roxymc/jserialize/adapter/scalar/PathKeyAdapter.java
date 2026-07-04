package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

import java.nio.file.Path;

public final class PathKeyAdapter extends AbstractKeyAdapter<Path> {
    private static final TypeRef<Path> TYPE = TypeRef.of(Path.class);

    public static final KeyAdapter<Path> INSTANCE = new PathKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private PathKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Path parse(String value) {
        return Path.of(value);
    }

    @Override
    public TypeRef<? extends Path> type() {
        return TYPE;
    }
}
