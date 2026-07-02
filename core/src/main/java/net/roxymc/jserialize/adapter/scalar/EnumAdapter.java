package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public final class EnumAdapter<E extends Enum<E>> implements TypeAdapter<E> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Factory FACTORY = Factory.<Enum>where(TypeUtils::isEnum, EnumAdapter::new);

    private final TypeRef<E> enumType;
    private final Class<E> enumClass;

    @SuppressWarnings("unchecked")
    public EnumAdapter(TypeRef<E> enumType) {
        this.enumType = enumType;
        this.enumClass = (Class<E>) enumType.getRawType();
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable E read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        String name = reader.readString();

        return Enum.valueOf(enumClass, name);
    }

    @Override
    public void write(Writer writer, @Nullable E value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.name());
    }

    @Override
    public TypeRef<? extends E> type() {
        return enumType;
    }
}
