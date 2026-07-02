package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.annotation.JSerializable;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public final class ObjectAdapter<T> implements TypeAdapter.Mutable<T> {
    private static final Factory FACTORY = factory(ClassModel.factory());
    private static final Factory ANNOTATED_FACTORY = annotatedFactory(ClassModel.factory());

    private final TypeRef<T> type;
    private final ClassModel<T> classModel;

    private ObjectAdapter(TypeRef<T> type, ClassModel<T> classModel) {
        this.type = type;
        this.classModel = classModel;
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(ClassModel.Factory factory) {
        return Factory.where(
                type -> !type.getRawType().isPrimitive(),
                type -> new ObjectAdapter<>(type, factory.create(type))
        );
    }

    public static Factory annotatedFactory() {
        return ANNOTATED_FACTORY;
    }

    public static Factory annotatedFactory(ClassModel.Factory factory) {
        return Factory.where(
                type -> !type.getRawType().isPrimitive() && type.getAnnotatedType().isAnnotationPresent(JSerializable.class),
                type -> new ObjectAdapter<>(type, factory.create(type))
        );
    }

    @Override
    public @Nullable T mutate(Reader reader, @Nullable T value, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            if (value != null) {
                throw new IllegalStateException("Cannot mutate value with null");
            }

            reader.readNull();
            return null;
        }

        try {
            @SuppressWarnings("NullableProblems") // IntelliJ has existential issues
            ObjectReader<T, ?> objectReader = new ObjectReader<>(classModel, type, value, ctx.formatUtils(), ctx);

            return objectReader.read(reader);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to read object of type: " + type.getType(), e);
        }
    }

    @Override
    public void write(Writer writer, @Nullable T value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        try {
            @SuppressWarnings("NullableProblems") // IntelliJ has existential issues
            ObjectWriter<T, ?> objectWriter = new ObjectWriter<>(classModel, type, value, ctx.formatUtils(), ctx);

            objectWriter.write(writer);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to write object of type: " + type.getType(), e);
        }
    }

    @Override
    public TypeRef<? extends T> type() {
        return type;
    }
}
