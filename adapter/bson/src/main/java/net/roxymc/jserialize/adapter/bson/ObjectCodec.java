package net.roxymc.jserialize.adapter.bson;

import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.util.TypeUtils;
import org.bson.BsonValue;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class ObjectCodec<T> implements Codec<T> {
    static final Class<BsonValue> RAW_TYPE = BsonValue.class;
    static final String ID_PROPERTY_NAME = "_id";
    static final DecoderContext DEFAULT_DECODER_CONTEXT = DecoderContext.builder().build();

    final ObjectAdapterEngine<T, BsonValue> engine;
    final ClassModel<T> classModel;
    final CodecRegistry codecRegistry;

    ObjectCodec(ClassModel<T> classModel, CodecRegistry codecRegistry) {
        this.engine = new ObjectAdapterEngine<>(classModel, new BsonUtils(this));
        this.classModel = classModel;
        this.codecRegistry = codecRegistry;
    }

    <U> Codec<U> getCodec(Type type) {
        @SuppressWarnings("unchecked")
        Class<U> rawType = (Class<U>) TypeUtils.rawType(type);

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return codecRegistry.get(rawType, List.of(parameterizedType.getActualTypeArguments()));
        }

        return codecRegistry.get(rawType);
    }

    @Override
    public T decode(org.bson.BsonReader reader, DecoderContext ctx) {
        try {
            return engine.read(new BsonReaderAdapter(this, reader, ctx), ReadContext.empty());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(org.bson.BsonWriter writer, T value, EncoderContext ctx) {
        try {
            engine.write(new BsonWriterAdapter(this, writer, ctx), value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        return classModel.clazz();
    }
}
