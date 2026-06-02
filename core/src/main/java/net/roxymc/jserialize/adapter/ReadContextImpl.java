package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.util.ObjectUtils;
import org.jspecify.annotations.Nullable;

final class ReadContextImpl implements ReadContext {
    private final TypeAdapters typeAdapters;
    private final @Nullable Object parent;
    private final @Nullable String key;
    private final FormatUtils<?> formatUtils;

    ReadContextImpl(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        this(typeAdapters, null, null, formatUtils);
    }

    private ReadContextImpl(
            TypeAdapters typeAdapters,
            @Nullable Object parent,
            @Nullable String key,
            FormatUtils<?> formatUtils
    ) {
        this.typeAdapters = typeAdapters;
        this.parent = parent;
        this.key = key;
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
        return new ReadContextImpl(typeAdapters, parent, key, formatUtils);
    }

    @Override
    public @Nullable String key() {
        return key;
    }

    @Override
    public ReadContext withKey(@Nullable String key) {
        return new ReadContextImpl(typeAdapters, parent, key, formatUtils);
    }

    @Override
    public FormatUtils<?> formatUtils() {
        return formatUtils;
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this)
                .add("parent=" + parent)
                .add("key=" + key)
                .toString();
    }
}
