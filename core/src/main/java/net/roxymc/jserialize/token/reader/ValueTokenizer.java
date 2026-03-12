package net.roxymc.jserialize.token.reader;

import net.roxymc.jserialize.token.NameToken;
import net.roxymc.jserialize.token.Token;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.ValueAccessor;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class ValueTokenizer<V> implements Tokenizer<V> {
    private final ValueAccessor<V> accessor;
    private final Deque<Entry<V>> stack = new ArrayDeque<>();

    public ValueTokenizer(V value, ValueAccessor<V> accessor) {
        this.accessor = accessor;
        push(value);
    }

    private void push(V value) {
        nonNull(value, "value");

        boolean isObject = accessor.isObject(value);
        if (isObject || accessor.isArray(value)) {
            stack.push(new ContainerEntry<>(isObject, value, accessor.getValues(value).iterator()));
        } else {
            stack.push(new ScalarEntry<>(value));
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    public TokenType peek() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException();
        }

        Entry<V> entry = stack.element();
        V value = entry.peek();

        if (entry instanceof ContainerEntry) {
            ContainerEntry<V> container = (ContainerEntry<V>) entry;

            if (!container.started) {
                return container.isObject ? TokenType.OBJECT_START : TokenType.ARRAY_START;
            }

            if (value == null && !container.hasNext()) {
                return container.isObject ? TokenType.OBJECT_END : TokenType.ARRAY_END;
            }
        }

        if (value != null) {
            if (entry instanceof ContainerEntry && ((ContainerEntry<V>) entry).isObject) {
                return TokenType.NAME;
            }

            if (accessor.isObject(value)) {
                return TokenType.OBJECT_START;
            }

            if (accessor.isArray(value)) {
                return TokenType.ARRAY_START;
            }

            return accessor.getTokenType(value);
        }

        // scalar is exhausted, pop it
        stack.pop();

        return peek();
    }

    @Override
    public Token next() {
        TokenType type = peek();
        Entry<V> entry = stack.element();

        if (type == TokenType.OBJECT_START || type == TokenType.ARRAY_START) {
            V current = entry.pop();

            if (current == null) {
                return type == TokenType.OBJECT_START ? Token.OBJECT_START : Token.ARRAY_START;
            }

            push(current);
            return next();
        }

        if (type == TokenType.OBJECT_END || type == TokenType.ARRAY_END) {
            stack.pop();
            return type == TokenType.OBJECT_END ? Token.OBJECT_END : Token.ARRAY_END;
        }

        V value = Objects.requireNonNull(entry.pop());

        if (type == TokenType.NAME) {
            push(value);
            return new NameToken(accessor.getName(value));
        }

        return accessor.toToken(value);
    }

    @Override
    public V nextValue() {
        TokenType type = peek();
        Entry<V> entry = stack.element();

        if (type.kind() == TokenType.Kind.MARKER) {
            throw new IllegalStateException(type + " does not support this operation");
        }

        if (type == TokenType.OBJECT_START || type == TokenType.ARRAY_START) {
            ContainerEntry<V> container = ((ContainerEntry<V>) entry);

            if (!container.started) {
                stack.pop();
                return container.container;
            }
        }

        return Objects.requireNonNull(entry.pop());
    }

    @Override
    public void skipValue() {
        nextValue();
    }

    private interface Entry<V> {
        boolean hasNext();

        @Nullable V peek();

        @Nullable V pop();
    }

    private static final class ScalarEntry<V> implements Entry<V> {
        private @Nullable V value;

        private ScalarEntry(V value) {
            this.value = nonNull(value, "value");
        }

        @Override
        public boolean hasNext() {
            return value != null;
        }

        @Override
        public @Nullable V peek() {
            return value;
        }

        @Override
        public @Nullable V pop() {
            V value = this.value;
            this.value = null;
            return value;
        }
    }

    private static final class ContainerEntry<V> implements Entry<V> {
        private final boolean isObject;
        private final V container;
        private final Iterator<? extends V> iterator;

        private boolean started = false;
        private @Nullable V value;

        private ContainerEntry(boolean isObject, V container, Iterator<? extends V> iterator) {
            this.isObject = isObject;
            this.container = nonNull(container, "container");
            this.iterator = nonNull(iterator, "iterator");
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public @Nullable V peek() {
            return value;
        }

        @Override
        public @Nullable V pop() {
            started = true;
            V value = this.value;
            this.value = iterator.hasNext() ? iterator.next() : null;
            return value;
        }
    }
}
