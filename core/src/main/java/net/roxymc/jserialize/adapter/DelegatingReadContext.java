package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.FormatUtils;
import org.jspecify.annotations.Nullable;

public class DelegatingReadContext implements ReadContext {
    private final ReadContext delegate;

    public DelegatingReadContext(ReadContext delegate) {
        this.delegate = delegate;
    }

    public final ReadContext delegate() {
        return delegate;
    }

    protected ReadContext wrap(ReadContext delegate) {
        return new DelegatingReadContext(delegate);
    }

    @Override
    public TypeAdapters typeAdapters() {
        return delegate.typeAdapters();
    }

    @Override
    public @Nullable Object parent() {
        return delegate.parent();
    }

    @Override
    public ReadContext withParent(@Nullable Object parent) {
        return wrap(delegate.withParent(parent));
    }

    @Override
    public @Nullable String key() {
        return delegate.key();
    }

    @Override
    public ReadContext withKey(@Nullable String key) {
        return wrap(delegate.withKey(key));
    }

    @Override
    public FormatUtils<?> formatUtils() {
        return delegate.formatUtils();
    }
}
