package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.annotation.JSerializable;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.type.TypeToken;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.Predicate;

public final class ObjectAdapter<T> implements TypeAdapter.Mutable<T> {
    private static final TypeAdapter.Factory FACTORY = factory(ClassModel.factory());
    private static final TypeAdapter.Factory ANNOTATED_FACTORY = annotatedFactory(ClassModel.factory());

    private final ClassModel.Factory factory;

    public ObjectAdapter(ClassModel.Factory factory) {
        this.factory = factory;
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(ClassModel.Factory factory) {
        return TypeAdapter.Factory.predicate(Predicate.not(TypeUtils::isPrimitive), new ObjectAdapter<>(factory));
    }

    public static TypeAdapter.Factory annotatedFactory() {
        return ANNOTATED_FACTORY;
    }

    public static TypeAdapter.Factory annotatedFactory(ClassModel.Factory factory) {
        return TypeAdapter.Factory.predicate(
                type -> !TypeUtils.isPrimitive(type) && type.getAnnotatedType().isAnnotationPresent(JSerializable.class),
                new ObjectAdapter<>(factory)
        );
    }

    @Override
    public @Nullable T mutate(Reader reader, TypeToken<? extends T> type, @Nullable T instance, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenType.NULL) {
            return instance;
        }

        try {
            ClassModel<T> classModel = factory.create(type);
            ObjectReader<T, ?> objectReader = new ObjectReader<>(classModel, type, instance, ctx.formatUtils(), ctx);

            return objectReader.read(reader);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to read object of type: " + type.getType(), e);
        }
    }

    @Override
    public void write(Writer writer, TypeToken<? extends T> type, @Nullable T instance, WriteContext ctx) throws IOException {
        if (instance == null) {
            writer.writeNull();
            return;
        }

        try {
            ClassModel<T> classModel = factory.create(type);
            ObjectWriter<T, ?> objectWriter = new ObjectWriter<>(classModel, type, instance, ctx.formatUtils(), ctx);

            objectWriter.write(writer);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to write object of type: " + type.getType(), e);
        }
    }
}
