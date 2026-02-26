package net.roxymc.jserialize.adapter.configurate;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.adapter.ObjectAdapterEngine;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.ReaderAdapter;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.util.Pair;
import net.roxymc.jserialize.util.TransformingIterator;
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
            ClassModel<?> classModel = objectSerializer.factory.create(GenericTypeReflector.erase(type));

            return readValue(node, classModel, type);
        }

        return PropertyValue.of(node.get(type));
    }

    private <U> PropertyValue<U> readValue(ConfigurationNode node, ClassModel<U> classModel, Type type) {
        ObjectAdapterEngine<U, ConfigurationNode> engine = new ObjectAdapterEngine<>(
                classModel, type, new ConfigurateUtils(node.options())
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
