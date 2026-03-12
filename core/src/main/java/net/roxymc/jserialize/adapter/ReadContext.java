package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public interface ReadContext {
    @ApiStatus.Internal
    static ReadContext of(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        return new ReadContextImpl(typeAdapters, formatUtils);
    }

    TypeAdapters typeAdapters();

    @Nullable Object parent();

    ReadContext withParent(@Nullable Object parent);

    @ApiStatus.Internal
    FormatUtils<?> formatUtils();
}
