package net.roxymc.jserialize.format.tree;

import net.roxymc.jserialize.AbstractWriter;
import net.roxymc.jserialize.token.ScalarToken;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenType.Kind;
import net.roxymc.jserialize.token.TokenTypes;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TreeWriter<V> extends AbstractWriter {
    private final TreeAccessor<V> accessor;

    private final V root;
    private final Deque<V> containers = new ArrayDeque<>();

    private @Nullable String pendingName;

    public TreeWriter(TreeAccessor<V> accessor, V root) {
        this.accessor = nonNull(accessor, "accessor");
        this.root = nonNull(root, "root");
    }

    private void checkNoPendingName() {
        if (pendingName != null) {
            throw new IllegalStateException("Previous name was not consumed");
        }
    }

    @Override
    public void write(TokenType.NonValued tokenType) throws IOException {
        if (tokenType == TokenTypes.OBJECT_START) {
            V container = currentValue();
            accessor.initObject(container);

            containers.push(container);
        } else if (tokenType == TokenTypes.OBJECT_END) {
            checkNoPendingName();

            V container = containers.peek();
            if (container == null || !accessor.isObject(container)) {
                throw new IllegalStateException("No object to end");
            }

            containers.pop();
        } else if (tokenType == TokenTypes.ARRAY_START) {
            V container = currentValue();
            accessor.initArray(container);

            containers.push(container);
        } else if (tokenType == TokenTypes.ARRAY_END) {
            checkNoPendingName();

            V container = containers.peek();
            if (container == null || !accessor.isArray(container)) {
                throw new IllegalStateException("No array to end");
            }

            containers.pop();
        } else if (tokenType.kind() == Kind.NULL) {
            accessor.setScalar(currentValue(), null);
        } else {
            throw notSupported(tokenType);
        }
    }

    @Override
    public <T> void write(TokenType.Valued<T> tokenType, T value) throws IOException {
        if (tokenType == TokenTypes.NAME) {
            checkNoPendingName();
            pendingName = (String) value;
            return;
        }

        accessor.setScalar(currentValue(), (ScalarToken<?>) tokenType.create(value));
    }

    public V currentValue() { // TODO temporary bridge for Configurate
        if (containers.isEmpty()) {
            return root;
        }

        V container = nonNull(containers.peek(), "container");

        if (pendingName != null) {
            if (!accessor.isObject(container)) {
                throw new IllegalStateException("Cannot write named value to a non-object");
            }

            String name = pendingName;
            pendingName = null;

            return accessor.appendEntry(container, name);
        }

        if (!accessor.isArray(container)) {
            throw new IllegalStateException("Cannot append to a non-array");
        }

        return accessor.appendElement(container);
    }
}
