package net.roxymc.jserialize.format.tree;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.*;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.*;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TreeReader<V> extends AbstractReader {
    private final TreeAccessor<V> accessor;

    private final Deque<Frame> frames = new ArrayDeque<>();
    private final Queue<TokenHolder> buffer = new ArrayDeque<>(2);

    public TreeReader(TreeAccessor<V> accessor, V root) {
        this.accessor = nonNull(accessor, "accessor");

        prepareValue(nonNull(root, "root"));
    }

    @Override
    public TokenType peek() {
        fillBufferIfNeeded();

        TokenHolder token = buffer.peek();
        return token != null ? token.token.type() : TokenTypes.END;
    }

    @Override
    public void read(TokenType.NonValued tokenType) throws IOException {
        checkToken(peek(), tokenType);

        buffer.remove();
    }

    @Override
    public <T> T read(TokenType.Valued<T> tokenType) throws IOException {
        checkToken(peek(), tokenType);

        @SuppressWarnings("unchecked")
        ValuedToken<T> token = (ValuedToken<T>) buffer.remove().token;
        return token.value();
    }

    @Override
    public void skipValue() {
        TokenType type = peek();

        if (!type.kind().marksValue()) {
            throw new IllegalStateException(type + " does not support this operation");
        }

        Token token = buffer.remove().token;

        if (token.type().kind() == TokenType.Kind.STRUCTURE_START) {
            frames.pop();
        }
    }

    private void fillBufferIfNeeded() {
        if (!buffer.isEmpty() || frames.isEmpty()) {
            return;
        }

        if (!frames.element().flushNext()) {
            frames.pop();
        }
    }

    private void prepareValue(V value) {
        TreeNode<V> node = accessor.nodeOf(value);

        if (node instanceof TreeNode.ObjectNode) {
            TreeNode.ObjectNode<V> objectNode = (TreeNode.ObjectNode<V>) node;

            buffer.add(new TokenHolder(Tokens.OBJECT_START, value));
            frames.push(new ObjectFrame(objectNode.entries()));
        } else if (node instanceof TreeNode.ArrayNode) {
            TreeNode.ArrayNode<V> arrayNode = (TreeNode.ArrayNode<V>) node;

            buffer.add(new TokenHolder(Tokens.ARRAY_START, value));
            frames.push(new ArrayFrame(arrayNode.elements()));
        } else if (node instanceof TreeNode.ScalarNode) {
            TreeNode.ScalarNode<V> scalarNode = (TreeNode.ScalarNode<V>) node;

            ScalarToken<?> token = scalarNode.token();
            buffer.add(new TokenHolder(token != null ? token : Tokens.NULL, value));
        }
    }

    private interface Frame {
        boolean flushNext();
    }

    public V currentValue() { // TODO temporary bridge for Configurate
        fillBufferIfNeeded();

        TokenHolder token = buffer.poll();
        V value = token != null ? token.value : null;

        if (value == null) {
            throw new IllegalStateException("Cannot retrieve value");
        }

        if (token.token.type().kind() == TokenType.Kind.STRUCTURE_START) {
            frames.pop();
        }

        return value;
    }

    private final class ObjectFrame implements Frame {
        private final Iterator<Map.Entry<String, ? extends V>> iterator;
        private boolean exhausted;

        ObjectFrame(Iterable<Map.Entry<String, ? extends V>> iterable) {
            this.iterator = iterable.iterator();
        }

        @Override
        public boolean flushNext() {
            if (exhausted) {
                throw new IllegalStateException("exhausted");
            }

            if (!iterator.hasNext()) {
                buffer.add(new TokenHolder(Tokens.OBJECT_END, null));
                exhausted = true;
                return false;
            }

            Map.Entry<String, ? extends V> entry = iterator.next();

            buffer.add(new TokenHolder(new NameToken(entry.getKey()), null));
            prepareValue(entry.getValue());
            return true;
        }
    }

    private final class ArrayFrame implements Frame {
        private final Iterator<? extends V> iterator;
        private boolean exhausted;

        ArrayFrame(Iterable<? extends V> iterable) {
            this.iterator = iterable.iterator();
        }

        @Override
        public boolean flushNext() {
            if (exhausted) {
                throw new IllegalStateException("exhausted");
            }

            if (!iterator.hasNext()) {
                buffer.add(new TokenHolder(Tokens.ARRAY_END, null));
                exhausted = true;
                return false;
            }

            prepareValue(iterator.next());
            return true;
        }
    }

    private final class TokenHolder {
        private final Token token;
        private final @Nullable V value;

        private TokenHolder(Token token, @Nullable V value) {
            this.token = token;
            this.value = value;
        }
    }
}
