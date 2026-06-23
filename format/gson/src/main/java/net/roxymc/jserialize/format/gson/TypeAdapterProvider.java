package net.roxymc.jserialize.format.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TypeAdapterProvider implements TypeAdapterFactory {
    private final TypeAdapters adapters;

    public TypeAdapterProvider(TypeAdapters adapters) {
        this.adapters = nonNull(adapters, "adapters");
    }

    @Override
    public <T> com.google.gson.@Nullable TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> gtype) {
        TypeToken<T> type = TypeToken.of(gtype.getType());

        TypeAdapter<T> adapter = this.adapters.get(type);
        if (adapter == null) {
            return null;
        }

        GsonTypeAdapters adapters = new GsonTypeAdapters(gson, this.adapters);
        return new WrappedTypeAdapter<>(type, adapter, adapters);
    }
}
