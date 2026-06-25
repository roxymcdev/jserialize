package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.adapter.object.MapLike;
import net.roxymc.jserialize.format.tree.TreeReader;
import net.roxymc.jserialize.format.tree.TreeWriter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.stream.Collectors;

final class ConfigurateUtils implements FormatUtils<ConfigurationNode> {
    static final ConfigurateUtils INSTANCE = new ConfigurateUtils();

    private ConfigurateUtils() {
    }

    static Reader newReader0(ConfigurationNode node) {
        return new TreeReader<>(ConfigurationNodeAccessor.INSTANCE, node);
    }

    static Writer newWriter0(ConfigurationNode node) {
        return new TreeWriter<>(ConfigurationNodeAccessor.INSTANCE, node);
    }

    @Override
    public Class<ConfigurationNode> rawType() {
        return ConfigurationNode.class;
    }

    @Override
    public Reader newReader(ConfigurationNode raw) {
        return newReader0(raw);
    }

    @Override
    public MapLike<ConfigurationNode> createMap(TypeAdapters typeAdapters, AnnotatedType mapType) {
        TypeRef<Map<?, ?>> typeRef = TypeRef.of(mapType);
        TypeAdapter<Map<?, ?>> mapAdapter = typeAdapters.getOrThrow(typeRef);

        return new MapLike<>() {
            private final ConfigurationNode node = CommentedConfigurationNode.root(
                    ((ConfigurateTypeAdapters) typeAdapters).options
            );

            @Override
            public void put(String key, @Nullable ConfigurationNode value) {
                if (value != null) {
                    node.node(key).from(value);
                }
            }

            @Override
            public void putAll(Map<?, ?> map, WriteContext ctx) throws IOException {
                CommentedConfigurationNode result = CommentedConfigurationNode.root(node.options());

                mapAdapter.write(newWriter0(result), typeRef, map, ctx);

                node.mergeFrom(result);
            }

            @Override
            public @Nullable Map<?, ?> asMap(@Nullable Map<?, ?> instance, ReadContext ctx) throws IOException {
                Reader readerAdapter = newReader0(node);

                if (!(mapAdapter instanceof TypeAdapter.Mutable)) {
                    return mapAdapter.read(readerAdapter, typeRef, ctx);
                }

                return ((TypeAdapter.Mutable<Map<?, ?>>) mapAdapter).mutate(readerAdapter, typeRef, instance, ctx);
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
