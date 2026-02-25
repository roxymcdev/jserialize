package net.roxymc.jserialize.adapter.configurate;

import net.roxymc.jserialize.adapter.ObjectAdapterEngine;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.ReaderAdapter;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.util.Pair;
import net.roxymc.jserialize.util.TransformingIterator;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

final class ConfigurateReaderAdapter implements ReaderAdapter<ConfigurationNode> {
    private final ConfigurationNode rootNode;
    private final ClassModel<?> classModel;

    ConfigurateReaderAdapter(ConfigurationNode rootNode, ClassModel<?> classModel) {
        this.rootNode = rootNode;
        this.classModel = classModel;
    }

    @Override
    public Iterable<Pair<String, @Nullable PropertyModel>> properties() {
        return () -> new TransformingIterator<>(rootNode.childrenMap().keySet().iterator(), key -> {
            String name = String.valueOf(key);
            return new Pair<>(name, classModel.properties().get(name));
        });
    }

    @Override
    public PropertyValue<?> readValue(String name, Type type) throws Throwable {
        ConfigurationNode node = rootNode.node(name);
        TypeSerializer<?> serializer = rootNode.options().serializers().get(type);

        if (serializer instanceof ObjectSerializer) {
            ObjectSerializer objectSerializer = (ObjectSerializer) serializer;
            ClassModel<?> classModel = objectSerializer.factory.create(TypeUtils.rawType(type));

            return readPropertyValue(node, classModel);
        }

        return PropertyValue.of(node.get(type));
    }

    private <U> PropertyValue<U> readPropertyValue(ConfigurationNode node, ClassModel<U> classModel) {
        ObjectAdapterEngine<U, ConfigurationNode> engine = new ObjectAdapterEngine<>(
                classModel, new ConfigurateUtils(node.options())
        );

        return (parent, instance) -> engine.read(
                new ConfigurateReaderAdapter(node, classModel),
                new ReadContext<>(parent, instance)
        );
    }

    @Override
    public ConfigurationNode readRawValue(String name) {
        return rootNode.node(name);
    }
}
