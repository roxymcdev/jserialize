package net.roxymc.jserialize.format.gson;

import com.google.gson.Gson;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

final class GsonTypeAdapters implements TypeAdapters {
    private final Gson gson;
    private final TypeAdapters adapters;

    GsonTypeAdapters(Gson gson, TypeAdapters adapters) {
        this.gson = gson;
        this.adapters = adapters;
    }

    @Override
    public <T> @Nullable TypeAdapter<T> get(TypeRef<T> type) {
        @SuppressWarnings("unchecked")
        com.google.gson.reflect.TypeToken<T> typeToken = (com.google.gson.reflect.TypeToken<T>) com.google.gson.reflect.TypeToken.get(type.getType());
        com.google.gson.TypeAdapter<T> adapter;

        try {
            adapter = gson.getAdapter(typeToken);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (adapter instanceof WrappedTypeAdapter) {
            return ((WrappedTypeAdapter<T>) adapter).typeAdapter;
        }

        return new WrappedGsonTypeAdapter<>(type, adapter);
    }

    @Override
    public <T> @Nullable KeyAdapter<T> getKey(TypeRef<T> type) {
        return adapters.getKey(type);
    }

    @Override
    public @Nullable <T> TypeAdapter<T> create(TypeRef<T> type) {
        return get(type);
    }

    @Override
    public @Nullable <T> KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
        return adapters.createKey(type, adapters);
    }
}
