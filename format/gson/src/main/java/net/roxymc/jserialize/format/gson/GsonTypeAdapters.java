package net.roxymc.jserialize.format.gson;

import com.google.gson.Gson;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;

final class GsonTypeAdapters implements TypeAdapters {
    private final Gson gson;

    GsonTypeAdapters(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> TypeAdapter<T> get(TypeToken<? extends T> type) {
        @SuppressWarnings("unchecked")
        com.google.gson.reflect.TypeToken<T> typeToken = (com.google.gson.reflect.TypeToken<T>) com.google.gson.reflect.TypeToken.get(type.getType());
        com.google.gson.TypeAdapter<T> adapter = gson.getAdapter(typeToken);

        if (adapter instanceof WrappedTypeAdapter) {
            return ((WrappedTypeAdapter<T>) adapter).typeAdapter;
        }

        return new WrappedGsonTypeAdapter<>(adapter);
    }
}
