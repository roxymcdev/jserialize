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
import java.nio.file.Path;

public final class PathAdapter implements TypeAdapter<Path> {
    private static final TypeRef<Path> TYPE = TypeRef.of(Path.class);

    public static final TypeAdapter<Path> INSTANCE = new PathAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private PathAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Path read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return Path.of(reader.readString());
    }

    @Override
    public void write(Writer writer, @Nullable Path value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }

    @Override
    public TypeRef<? extends Path> type() {
        return TYPE;
    }
}
