package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;

public class DelegatingWriteContext implements WriteContext {
    private final WriteContext delegate;

    protected DelegatingWriteContext(WriteContext delegate) {
        this.delegate = delegate;
    }

    public final WriteContext delegate() {
        return delegate;
    }

    @Override
    public TypeAdapters typeAdapters() {
        return delegate.typeAdapters();
    }

    @Override
    public FormatUtils<?> formatUtils() {
        return delegate.formatUtils();
    }
}
