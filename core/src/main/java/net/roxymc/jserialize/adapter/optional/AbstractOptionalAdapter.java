package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedParameterizedType;
import java.util.Optional;

import static net.roxymc.jserialize.util.TypeUtils.resolveTypeParams;

abstract class AbstractOptionalAdapter<O, V> implements TypeAdapter<O> {
    private final TypeRef<O> optionalType;
    private final TypeRef<V> valueType;

    protected AbstractOptionalAdapter(Class<O> optionalType, Class<V> valueType) {
        this.optionalType = TypeRef.of(optionalType);
        this.valueType = TypeRef.of(valueType);
    }

    protected AbstractOptionalAdapter(TypeRef<O> optionalType) {
        AnnotatedParameterizedType ptype = resolveTypeParams(optionalType, Optional.class);

        this.optionalType = optionalType;
        this.valueType = TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
    }

    protected abstract O createOptional(@Nullable V value);

    protected abstract @Nullable V getOptional(O optional);

    @Override
    public @Nullable O read(Reader reader, ReadContext ctx) throws IOException {
        V value = ctx.read(reader, valueType);

        return createOptional(value);
    }

    @Override
    public void write(Writer writer, @Nullable O optional, WriteContext ctx) throws IOException {
        V value = optional != null ? getOptional(optional) : null;

        ctx.write(writer, valueType, value);
    }

    @Override
    public TypeRef<? extends O> type() {
        return optionalType;
    }
}
