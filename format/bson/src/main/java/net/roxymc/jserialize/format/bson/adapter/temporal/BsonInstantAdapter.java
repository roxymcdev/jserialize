package net.roxymc.jserialize.format.bson.adapter.temporal;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.format.bson.token.BsonTokenTypes;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;

public final class BsonInstantAdapter implements TypeAdapter<Instant> {
    private static final TypeRef<Instant> TYPE = TypeRef.of(Instant.class);

    public static final TypeAdapter<Instant> INSTANCE = new BsonInstantAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private BsonInstantAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Instant read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return Instant.ofEpochMilli(reader.read(BsonTokenTypes.DATE_TIME));
    }

    @Override
    public void write(Writer writer, @Nullable Instant value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.write(BsonTokenTypes.DATE_TIME, value.toEpochMilli());
    }

    @Override
    public TypeRef<? extends Instant> type() {
        return TYPE;
    }
}
