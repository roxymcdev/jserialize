package net.roxymc.jserialize.adapter.configurate;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.adapter.ObjectAdapterEngine;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.model.ClassModel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ObjectSerializer implements TypeSerializer<Object> {
    static final Class<ConfigurationNode> RAW_TYPE = ConfigurationNode.class;

    final ClassModel.Factory factory;

    public ObjectSerializer(ClassModel.Factory factory) {
        this.factory = factory;
    }

    @Override
    public Object deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            ClassModel<?> classModel = factory.create(GenericTypeReflector.erase(type));
            ObjectAdapterEngine<?, ConfigurationNode> engine = new ObjectAdapterEngine<>(
                    classModel, type, new ConfigurateUtils(node.options())
            );

            return engine.read(new ConfigurateReaderAdapter(node, classModel), ReadContext.empty());
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable Object value, ConfigurationNode node) throws SerializationException {
        if (value == null) {
            node.set(null);
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            ClassModel<Object> classModel = (ClassModel<Object>) factory.create(GenericTypeReflector.erase(type));
            ObjectAdapterEngine<Object, ConfigurationNode> engine = new ObjectAdapterEngine<>(
                    classModel, type, new ConfigurateUtils(node.options())
            );

            engine.write(new ConfigurateWriterAdapter(node), value);
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }
}
