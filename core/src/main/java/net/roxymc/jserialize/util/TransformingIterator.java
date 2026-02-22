package net.roxymc.jserialize.util;

import java.util.Iterator;
import java.util.function.Function;

public final class TransformingIterator<T, U> implements Iterator<U> {
    private final Iterator<T> iterator;
    private final Function<T, U> transformer;

    public TransformingIterator(Iterator<T> iterator, Function<T, U> transformer) {
        this.iterator = iterator;
        this.transformer = transformer;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public U next() {
        return transformer.apply(iterator.next());
    }
}
