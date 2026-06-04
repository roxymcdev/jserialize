package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.TypeAdapters;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;

@ApiStatus.Internal
public interface FormatUtils<R> {
    default @Nullable String idPropertyName() {
        return null;
    }

    Class<R> rawType();

    Reader newReader(R raw);

    MapLike<R> createMap(TypeAdapters typeAdapters, AnnotatedType mapType);
}
