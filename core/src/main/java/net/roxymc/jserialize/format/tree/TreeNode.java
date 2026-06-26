package net.roxymc.jserialize.format.tree;

import net.roxymc.jserialize.token.ScalarToken;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public interface TreeNode<V> {
    static <V> TreeNode<V> object(Iterable<Map.Entry<String, ? extends V>> entries) {
        return new ObjectNode<>(entries);
    }

    static <V> TreeNode<V> array(Iterable<? extends V> elements) {
        return new ArrayNode<>(elements);
    }

    static <V> TreeNode<V> scalar(@Nullable ScalarToken<?> token) {
        @SuppressWarnings("unchecked")
        TreeNode<V> node = (TreeNode<V>) (token != null ? new ScalarNode<>(token) : ScalarNode.NULL);
        return node;
    }

    final class ObjectNode<V> implements TreeNode<V> {
        private final Iterable<Map.Entry<String, ? extends V>> entries;

        private ObjectNode(Iterable<Map.Entry<String, ? extends V>> entries) {
            this.entries = nonNull(entries, "entries");
        }

        public Iterable<Map.Entry<String, ? extends V>> entries() {
            return entries;
        }
    }

    final class ArrayNode<V> implements TreeNode<V> {
        private final Iterable<? extends V> elements;

        private ArrayNode(Iterable<? extends V> elements) {
            this.elements = nonNull(elements, "elements");
        }

        public Iterable<? extends V> elements() {
            return elements;
        }
    }

    final class ScalarNode<V> implements TreeNode<V> {
        private static final ScalarNode<?> NULL = new ScalarNode<>(null);

        private final @Nullable ScalarToken<?> token;

        private ScalarNode(@Nullable ScalarToken<?> token) {
            this.token = token;
        }

        public @Nullable ScalarToken<?> token() {
            return token;
        }
    }
}
