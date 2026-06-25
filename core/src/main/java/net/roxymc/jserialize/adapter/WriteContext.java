package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface WriteContext {
    @ApiStatus.Internal
    static WriteContext of(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        return new WriteContextImpl(typeAdapters, formatUtils);
    }

    TypeAdapters typeAdapters();

    default <T> void write(Writer writer, Class<T> type, @Nullable T value) throws IOException {
        write(writer, TypeRef.of(type), value);
    }

    default <T> void write(Writer writer, TypeRef<T> type, @Nullable T value) throws IOException {
        typeAdapters().getOrThrow(type).write(writer, type, value, this);
    }

    @ApiStatus.Internal
    FormatUtils<?> formatUtils();
}
