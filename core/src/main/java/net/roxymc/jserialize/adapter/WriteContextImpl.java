package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;

final class WriteContextImpl implements WriteContext {
    private final TypeAdapters typeAdapters;
    private final FormatUtils<?> formatUtils;

    WriteContextImpl(TypeAdapters typeAdapters, FormatUtils<?> formatUtils) {
        this.typeAdapters = typeAdapters;
        this.formatUtils = formatUtils;
    }

    @Override
    public TypeAdapters typeAdapters() {
        return typeAdapters;
    }

    @Override
    public FormatUtils<?> formatUtils() {
        return formatUtils;
    }
}
