package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;
import org.jetbrains.annotations.ApiStatus;

public interface WriteContext {
    @ApiStatus.Internal
    static WriteContext of(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        return new WriteContextImpl(typeAdapters, formatUtils);
    }

    TypeAdapters typeAdapters();

    @ApiStatus.Internal
    FormatUtils<?> formatUtils();
}
