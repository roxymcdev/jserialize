package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;

public final class URIAdapter implements TypeAdapter<URI> {
    private static final TypeRef<URI> TYPE = TypeRef.of(URI.class);

    public static final TypeAdapter<URI> INSTANCE = new URIAdapter();
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.exact(INSTANCE);

    private URIAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable URI read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return URI.create(reader.readString());
    }

    @Override
    public void write(Writer writer, @Nullable URI value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }

    @Override
    public TypeRef<? extends URI> type() {
        return TYPE;
    }
}
