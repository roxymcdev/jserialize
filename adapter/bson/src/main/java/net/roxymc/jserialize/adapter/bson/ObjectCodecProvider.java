package net.roxymc.jserialize.adapter.bson;

import net.roxymc.jserialize.model.ClassModel;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public final class ObjectCodecProvider implements CodecProvider {
    private final ClassModel.Factory factory;

    public ObjectCodecProvider(ClassModel.Factory factory) {
        this.factory = factory;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        try {
            ClassModel<T> classModel = factory.create(clazz);

            return new ObjectCodec<>(classModel, registry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
