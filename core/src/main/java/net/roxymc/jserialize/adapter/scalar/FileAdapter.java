package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class FileAdapter implements TypeAdapter<File> {
    private static final TypeRef<File> TYPE = TypeRef.of(File.class);

    public static final TypeAdapter<File> INSTANCE = new FileAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private FileAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable File read(Reader reader, ReadContext ctx) throws IOException {
        Path path = ctx.read(reader, Path.class);
        return path != null ? path.toFile() : null;
    }

    @Override
    public void write(Writer writer, @Nullable File value, WriteContext ctx) throws IOException {
        Path path = value != null ? value.toPath() : null;
        ctx.write(writer, Path.class, path);
    }

    @Override
    public TypeRef<? extends File> type() {
        return TYPE;
    }
}
