package net.roxymc.jserialize.format.tree;

import net.roxymc.jserialize.token.ScalarToken;
import org.jspecify.annotations.Nullable;

public interface TreeAccessor<V> {
    TreeNode<V> nodeOf(V value);

    boolean isObject(V value);

    void initObject(V value);

    boolean isArray(V value);

    void initArray(V value);

    void setScalar(V value, @Nullable ScalarToken<?> token);

    V appendEntry(V object, String name);

    V appendElement(V array);
}
