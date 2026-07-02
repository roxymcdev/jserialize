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

public final class BsonUUIDAdapter implements TypeAdapter<UUID> {
    private static final TypeRef<UUID> TYPE = TypeRef.of(UUID.class);
    private static final Factory FACTORY = factory(UuidRepresentation.STANDARD);

    private final UuidRepresentation uuidRepresentation;

    public BsonUUIDAdapter(UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = nonNull(uuidRepresentation, "uuidRepresentation");
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(UuidRepresentation uuidRepresentation) {
        return Factory.exact(new BsonUUIDAdapter(uuidRepresentation));
    }

    @Override
    public @Nullable UUID read(Reader reader, ReadContext context) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return reader.read(BsonTokenTypes.BINARY).asUuid(uuidRepresentation);
    }

    @Override
    public void write(Writer writer, @Nullable UUID value, WriteContext context) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.write(BsonTokenTypes.BINARY, new BsonBinary(value, uuidRepresentation));
    }

    @Override
    public TypeRef<? extends UUID> type() {
        return TYPE;
    }
}
