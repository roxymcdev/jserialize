package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.WriteContext;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

@ApiStatus.Internal
public interface MapLike<R> {
    void put(String key, @Nullable R value);

    void putAll(Map<?, ?> map, WriteContext ctx) throws IOException;

    @Nullable Map<?, ?> asMap(@Nullable Map<?, ?> instance, ReadContext ctx) throws IOException;

    Map<String, R> asRawMap();
}
