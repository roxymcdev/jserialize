package net.roxymc.jserialize.format.bson.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.format.bson.token.BsonTokenTypes;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.BsonBinary;
import org.bson.UuidRepresentation;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

public final class BsonUUIDAdapter implements TypeAdapter<UUID> {
    private static final TypeAdapter.Factory FACTORY = factory(UuidRepresentation.STANDARD);

    private final UuidRepresentation uuidRepresentation;

    public BsonUUIDAdapter(UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = nonNull(uuidRepresentation, "uuidRepresentation");
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(UuidRepresentation uuidRepresentation) {
        return TypeAdapter.Factory.exactRaw(UUID.class, new BsonUUIDAdapter(uuidRepresentation));
    }

    @Override
    public @Nullable UUID read(Reader reader, TypeRef<? extends UUID> type, ReadContext context) throws IOException {
        checkAssignable(UUID.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return reader.read(BsonTokenTypes.BINARY).asUuid(uuidRepresentation);
    }

    @Override
    public void write(Writer writer, TypeRef<? extends UUID> type, @Nullable UUID value, WriteContext context) throws IOException {
        checkAssignable(UUID.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.write(BsonTokenTypes.BINARY, new BsonBinary(value, uuidRepresentation));
    }
}
