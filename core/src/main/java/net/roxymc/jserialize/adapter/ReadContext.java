package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

@ApiStatus.NonExtendable
public interface ReadContext {
    @ApiStatus.Internal
    static ReadContext of(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        return new ReadContextImpl(typeAdapters, formatUtils);
    }

    TypeAdapters typeAdapters();

    @Nullable Object parent();

    ReadContext withParent(@Nullable Object parent);

    @Nullable String key();

    ReadContext withKey(@Nullable String key);

    default <T> @Nullable T read(Reader reader, Class<T> type) throws IOException {
        return read(reader, TypeRef.of(type));
    }

    default <T> @Nullable T read(Reader reader, TypeRef<T> type) throws IOException {
        return typeAdapters().getOrThrow(type).read(reader, this);
    }

    @ApiStatus.Internal
    FormatUtils<?> formatUtils();
}
