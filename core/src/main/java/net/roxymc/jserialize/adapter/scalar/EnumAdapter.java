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

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.predicate;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

public final class EnumAdapter implements TypeAdapter<Enum<?>> {
    private static final TypeAdapter.Factory FACTORY = predicate(type -> TypeUtils.isEnum(type.getRawType()), new EnumAdapter());

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Enum<?> read(Reader reader, TypeRef<? extends Enum<?>> type, ReadContext ctx) throws IOException {
        checkAssignable(Enum.class, type.getType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        String name = reader.readString();

        return Enum.valueOf(type.getRawType().asSubclass(Enum.class), name);
    }

    @Override
    public void write(Writer writer, TypeRef<? extends Enum<?>> type, @Nullable Enum<?> value, WriteContext ctx) throws IOException {
        checkAssignable(Enum.class, type.getType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.name());
    }
}
