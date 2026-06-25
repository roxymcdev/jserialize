package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.format.tree.TreeAccessor;
import net.roxymc.jserialize.format.tree.TreeNode;
import net.roxymc.jserialize.token.*;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

final class ConfigurationNodeAccessor implements TreeAccessor<ConfigurationNode> {
    static final ConfigurationNodeAccessor INSTANCE = new ConfigurationNodeAccessor();

    private ConfigurationNodeAccessor() {
    }

    private static ScalarToken<?> wrapAsToken(Object scalar) {
        if (scalar instanceof String) {
            return new StringToken((String) scalar);
        } else if (scalar instanceof Boolean) {
            return BooleanToken.of((boolean) scalar);
        } else if (scalar instanceof Float || scalar instanceof Double) {
            return new DoubleToken(((Number) scalar).doubleValue());
        } else if (scalar instanceof Long) {
            return new LongToken((long) scalar);
        } else if (scalar instanceof Number) {
            return new IntToken(((Number) scalar).intValue());
        } else if (scalar instanceof byte[]) {
            return new BinaryToken((byte[]) scalar);
        }

        throw new IllegalArgumentException("Unsupported scalar: " + scalar);
    }

    @Override
    public TreeNode<ConfigurationNode> nodeOf(ConfigurationNode value) {
        if (value.isMap()) {
            return TreeNode.object(value.childrenMap().entrySet().stream()
                    .map(e -> Map.entry(String.valueOf(e.getKey()), e.getValue()))
                    .collect(Collectors.toList())
            );
        }

        if (value.isList()) {
            return TreeNode.array(value.childrenList());
        }

        Object scalar = value.rawScalar();
        return TreeNode.scalar(scalar != null ? wrapAsToken(scalar) : null);
    }

    @Override
    public boolean isObject(ConfigurationNode value) {
        return value.isMap();
    }

    @Override
    public void initObject(ConfigurationNode value) {
        value.raw(Collections.emptyMap());
    }

    @Override
    public boolean isArray(ConfigurationNode value) {
        return value.isList();
    }

    @Override
    public void initArray(ConfigurationNode value) {
        value.raw(Collections.emptyList());
    }

    @Override
    public void setScalar(ConfigurationNode value, @Nullable ScalarToken<?> token) {
        value.raw(token != null ? token.value() : null);
    }

    @Override
    public ConfigurationNode appendEntry(ConfigurationNode object, String name) {
        return object.node(name);
    }

    @Override
    public ConfigurationNode appendElement(ConfigurationNode array) {
        return array.appendListNode();
    }
}