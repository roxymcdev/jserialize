package net.roxymc.jserialize.adapter.bson;

import io.leangen.geantyref.TypeFactory;
import net.roxymc.jserialize.model.ClassModel;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Type;
import java.util.List;

public final class ObjectCodecProvider implements CodecProvider {
    private final ClassModel.Factory factory;

    public ObjectCodecProvider(ClassModel.Factory factory) {
        this.factory = factory;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return get(clazz, List.of(), registry);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
        try {
            ClassModel<T> classModel = factory.create(clazz);
            Type type;

            if (!typeArguments.isEmpty()) {
                Type[] typeArgs = typeArguments.toArray(Type[]::new);

                if (clazz.getDeclaringClass() == null) {
                    type = TypeFactory.parameterizedClass(clazz, typeArgs);
                } else {
                    type = TypeFactory.parameterizedInnerClass(clazz.getDeclaringClass(), clazz, typeArgs);
                }
            } else {
                type = clazz;
            }

            return new ObjectCodec<>(classModel, type, registry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
