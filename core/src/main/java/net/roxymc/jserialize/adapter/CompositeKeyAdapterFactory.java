package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class CompositeKeyAdapterFactory implements KeyAdapter.Factory {
    private final KeyAdapter.Factory[] factories;

    CompositeKeyAdapterFactory(KeyAdapter.Factory[] factories) {
        this.factories = nonNull(factories, "factories").clone();
    }

    @Override
public <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
        for (KeyAdapter.Factory factory : factories) {
            KeyAdapter<T> adapter = factory.createKey(type, adapters);

            if (adapter != null) {
                return adapter;
            }
        }

        return null;
    }
}
