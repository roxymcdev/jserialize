package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class FileKeyAdapter implements KeyAdapter<File> {
    private static final TypeRef<File> TYPE = TypeRef.of(File.class);

    private static final Factory FACTORY = Factory.exactRaw(File.class, ($, adapters) ->
            new FileKeyAdapter(adapters.getKeyOrThrow(Path.class))
    );

    private final KeyAdapter<Path> pathAdapter;

    private FileKeyAdapter(KeyAdapter<Path> pathAdapter) {
        this.pathAdapter = nonNull(pathAdapter, "pathAdapter");
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable File decode(@Nullable String value) {
        Path path = pathAdapter.decode(value);
        return path != null ? path.toFile() : null;
    }

    @Override
    public String encode(@Nullable File value) {
        Path path = value != null ? value.toPath() : null;
        return pathAdapter.encode(path);
    }

    @Override
    public TypeRef<? extends File> type() {
        return TYPE;
    }
}
