package net.roxymc.jserialize.adapter.configurate;

import net.roxymc.jserialize.adapter.FormatUtils;
import net.roxymc.jserialize.adapter.MapLike;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class ConfigurateUtils implements FormatUtils<ConfigurationNode> {
    private final ConfigurationOptions options;

    ConfigurateUtils(ConfigurationOptions options) {
        this.options = options;
    }

    @Override
    public Class<ConfigurationNode> rawType() {
        return ObjectSerializer.RAW_TYPE;
    }

    @Override
    public MapLike<ConfigurationNode> createMap(Type mapType) {
        return new MapLike<>() {
            private final ConfigurationNode node = CommentedConfigurationNode.root(options);

            @Override
            public void put(String key, ConfigurationNode value) {
                node.node(key).from(value);
            }

            @Override
            public void putAll(Map<?, ?> map) throws Throwable {
                CommentedConfigurationNode mapNode = CommentedConfigurationNode.root(node.options());
                mapNode.set(mapType, map);

                node.mergeFrom(mapNode);
            }

            @Override
            public Map<?, ?> asMap() throws Throwable {
                return (Map<?, ?>) Objects.requireNonNullElse(node.get(mapType), Map.of());
            }

            @Override
            public Map<String, ConfigurationNode> asRawMap() {
                return node.childrenMap().entrySet().stream().collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
            }
        };
    }
}
