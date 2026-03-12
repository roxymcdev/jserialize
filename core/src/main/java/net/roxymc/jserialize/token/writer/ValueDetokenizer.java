package net.roxymc.jserialize.token.writer;

import net.roxymc.jserialize.token.NameToken;
import net.roxymc.jserialize.token.ScalarToken;
import net.roxymc.jserialize.token.Token;
import net.roxymc.jserialize.token.ValueAccessor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

public final class ValueDetokenizer<V> implements Detokenizer<V> {
    private final V root;
    private final Deque<V> stack = new ArrayDeque<>();
    private final ValueAccessor<V> accessor;

    private @Nullable String pendingName;

    public ValueDetokenizer(V value, ValueAccessor<V> accessor) {
        this.root = value;
        this.accessor = accessor;
    }

    private void checkNoPendingName() {
        if (pendingName != null) {
            throw new IllegalStateException("previous name was not consumed");
        }
    }

    @Override
    public void accept(Token token) {
        if (token instanceof NameToken) {
            checkNoPendingName();
            this.pendingName = ((NameToken) token).value();
            return;
        }

        if (token == Token.OBJECT_END) {
            checkNoPendingName();

            V container = stack.peek();
            if (container == null || !accessor.isObject(container)) {
                throw new IllegalStateException("no object to end");
            }

            stack.pop();
            return;
        }

        if (token == Token.ARRAY_END) {
            V container = stack.peek();
            if (container == null || !accessor.isArray(container)) {
                throw new IllegalStateException("no array to end");
            }

            stack.pop();
            return;
        }

        if (token instanceof ScalarToken) {
            accessor.setScalar(value(), ((ScalarToken<?>) token).value());
            return;
        }

        if (token == Token.OBJECT_START) {
            V container = value();
            accessor.setScalar(container, Collections.emptyMap());
            stack.push(container);
            return;
        }

        if (token == Token.ARRAY_START) {
            V container = value();
            accessor.setScalar(container, Collections.emptyList());
            stack.push(container);
            return;
        }

        throw new IllegalArgumentException("Unsupported token: " + token);
    }

    @Override
    public V value() {
        if (stack.isEmpty()) {
            return root;
        }

        V container = stack.peek();

        if (pendingName != null) {
            if (!accessor.isObject(container)) {
                throw new IllegalStateException("cannot write named value to a non-object");
            }

            String name = pendingName;
            pendingName = null;
            return accessor.objectAppend(container, name);
        }

        if (accessor.isArray(container)) {
            throw new IllegalStateException("cannot append to a non-array");
        }

        return accessor.listAppend(container);
    }
}
