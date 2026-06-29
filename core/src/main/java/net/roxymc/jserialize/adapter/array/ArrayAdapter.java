package net.roxymc.jserialize.adapter.array;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.predicate;

public final class ArrayAdapter implements TypeAdapter<Object> {
    private static final TypeAdapter.Factory FACTORY = predicate(type -> type.getRawType().isArray(), new ArrayAdapter());

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Object read(Reader reader, TypeRef<? extends Object> type, ReadContext ctx) throws IOException {
        if (!type.getRawType().isArray()) {
            throw new IllegalStateException(type.getRawType() + " is not an array");
        }

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        TypeRef<Object> componentType = resolveComponentType(type);
        TypeReader<Object> componentReader = ctx.typeAdapters().getOrThrow(componentType);

        reader.readArrayStart();

        List<@Nullable Object> buffer = new ArrayList<>();

        for (int index = 0; reader.peek() != TokenTypes.ARRAY_END; index++) {
            buffer.add(componentReader.read(reader, componentType, ctx));
        }

        reader.readArrayEnd();

        Object array = Array.newInstance(componentType.getRawType(), buffer.size());

        for (int i = 0; i < buffer.size(); i++) {
            Array.set(array, i, buffer.get(i));
        }

        return array;
    }

    @Override
    public void write(Writer writer, TypeRef<? extends Object> type, @Nullable Object value, WriteContext ctx) throws IOException {
        if (!type.getRawType().isArray()) {
            throw new IllegalStateException(type.getRawType() + " is not an array");
        }

        if (value == null) {
            writer.writeNull();
            return;
        }

        TypeRef<Object> componentType = resolveComponentType(type);
        TypeWriter<Object> componentWriter = ctx.typeAdapters().getOrThrow(componentType);

        writer.writeArrayStart();

        int length = Array.getLength(value);

        for (int i = 0; i < length; i++) {
            componentWriter.write(writer, componentType, Array.get(value, i), ctx);
        }

        writer.writeArrayEnd();
    }

    private TypeRef<Object> resolveComponentType(TypeRef<?> arrayType) {
        AnnotatedType type = arrayType.getAnnotatedType();
        if (!(type instanceof AnnotatedArrayType)) {
            throw new IllegalStateException(arrayType.getType() + " is not an array type");
        }

        AnnotatedArrayType atype = (AnnotatedArrayType) type;

        return TypeRef.of(atype.getAnnotatedGenericComponentType());
    }
}