package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public final class URLAdapter implements TypeAdapter<URL> {
    private static final TypeRef<URL> TYPE = TypeRef.of(URL.class);

    public static final TypeAdapter<URL> INSTANCE = new URLAdapter();
    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.exact(INSTANCE);

    private URLAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable URL read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return URI.create(reader.readString()).toURL();
    }

    @Override
    public void write(Writer writer, @Nullable URL value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }

    @Override
    public TypeRef<? extends URL> type() {
        return TYPE;
    }
}
