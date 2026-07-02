package net.roxymc.jserialize.adapter.array;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class ArrayAdapter implements TypeAdapter<Object> {
    private static final Factory FACTORY = Factory.where(type -> type.getRawType().isArray(), ArrayAdapter::new);

    private final TypeRef<?> arrayType;
    private final TypeRef<Object> componentType;

    private ArrayAdapter(TypeRef<?> arrayType) {
        this.arrayType = arrayType;
        this.componentType = TypeRef.of(((AnnotatedArrayType) arrayType).getAnnotatedGenericComponentType());
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Object read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        TypeReader<Object> componentReader = ctx.typeAdapters().getOrThrow(componentType);

        reader.readArrayStart();

        List<@Nullable Object> buffer = new ArrayList<>();

        for (int index = 0; reader.peek() != TokenTypes.ARRAY_END; index++) {
            buffer.add(componentReader.read(reader, ctx));
        }

        reader.readArrayEnd();

        Object array = Array.newInstance(componentType.getRawType(), buffer.size());

        for (int i = 0; i < buffer.size(); i++) {
            Array.set(array, i, buffer.get(i));
        }

        return array;
    }

    @Override
    public void write(Writer writer, @Nullable Object value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        TypeWriter<Object> componentWriter = ctx.typeAdapters().getOrThrow(componentType);

        writer.writeArrayStart();

        int length = Array.getLength(value);

        for (int i = 0; i < length; i++) {
            componentWriter.write(writer, Array.get(value, i), ctx);
        }

        writer.writeArrayEnd();
    }

    @Override
    public TypeRef<?> type() {
        return arrayType;
    }
}