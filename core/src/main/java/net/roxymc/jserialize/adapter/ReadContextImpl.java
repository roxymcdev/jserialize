package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;
import org.jspecify.annotations.Nullable;

final class ReadContextImpl implements ReadContext {
    private final TypeAdapters typeAdapters;
    private final @Nullable Object parent;
    private final FormatUtils<?> formatUtils;

    ReadContextImpl(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        this(typeAdapters, null, formatUtils);
    }

    private ReadContextImpl(
            TypeAdapters typeAdapters,
            @Nullable Object parent,
            FormatUtils<?> formatUtils
    ) {
        this.typeAdapters = typeAdapters;
        this.parent = parent;
        this.formatUtils = formatUtils;
    }

    @Override
    public TypeAdapters typeAdapters() {
        return typeAdapters;
    }

    @Override
    public @Nullable Object parent() {
        return parent;
    }

    @Override
    public ReadContext withParent(@Nullable Object parent) {
        return new ReadContextImpl(typeAdapters, parent, formatUtils);
    }

    @Override
    public FormatUtils<?> formatUtils() {
        return formatUtils;
    }
}
