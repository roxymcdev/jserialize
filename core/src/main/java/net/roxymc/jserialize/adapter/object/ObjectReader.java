package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.KeyDecoder;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.creator.InstanceCreator;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

final class ObjectReader<T, R> {
    private final ObjectAdapter<T> adapter;
    private final @Nullable T instance;
    private final FormatUtils<R> formatUtils;
    private final ReadContext context;

    ObjectReader(ObjectAdapter<T> adapter, @Nullable T instance, FormatUtils<R> formatUtils, ReadContext context) {
        this.adapter = adapter;
        this.instance = instance;
        this.formatUtils = formatUtils;
        this.context = context;
    }

    T read(Reader reader) throws Throwable {
        reader.readObjectStart();

        InstanceCreator.Builder<T> builder = InstanceCreator.builder(adapter.classModel);

        adapter.classModel.properties().getOptional(PropertyKind.PARENT).ifPresent(property ->
                builder.property(property, context.parent())
        );
        adapter.classModel.properties().getOptional(PropertyKind.KEY).ifPresent(property -> {
            TypeRef<?> type = resolveReadType(property);
            if (type == null) {
                return;
            }

            KeyDecoder<?> decoder = context.typeAdapters().getKeyOrThrow(type);

            builder.property(property, decoder.decode(context.key()));
        });

        PropertyModel extrasProperty = adapter.classModel.properties().get(PropertyKind.EXTRAS);
        MapLike<R> extrasMap;

        if (extrasProperty != null) {
            TypeRef<?> mapType = resolveReadType(extrasProperty);

            extrasMap = mapType != null ? formatUtils.createMap(context.typeAdapters(), mapType.getAnnotatedType()) : null;
        } else {
            extrasMap = null;
        }

        while (reader.peek() == TokenTypes.NAME) {
            String name = reader.readName();

            try {
                PropertyModel property = resolveProperty(name);

                if (property != null) {
                    PropertyValue<?> value = readProperty(reader, property);

                    if (value != null) {
                        builder.property(property, value);
                        continue;
                    }
                }

                if (extrasMap != null) {
                    extrasMap.put(name, readRawValue(reader));
                    continue;
                }

                reader.skipValue();
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to read property: " + name, ex);
            }
        }

        reader.readObjectEnd();

        if (extrasMap != null) {
            builder.property(extrasProperty, (PropertyValue.Mutable<Map<?, ?>>) (parent, instance) -> extrasMap.asMap(
                    instance, context.withParent(parent).withKey(extrasProperty.name())
            ));
        }

        InstanceCreator<T> creator = builder.build();
        return instance == null ? creator.createInstance() : creator.populate(instance);
    }

    private @Nullable PropertyModel resolveProperty(String name) {
        String idPropertyName = formatUtils.idPropertyName();
        if (idPropertyName == null) {
            return adapter.classModel.properties().get(name);
        }

        if (name.equals(idPropertyName)) {
            return adapter.classModel.properties().get(PropertyKind.ID);
        }

        PropertyModel property = adapter.classModel.properties().get(name);
        return property != null && property.kind() != PropertyKind.ID ? property : null;
    }

    private @Nullable PropertyValue<?> readProperty(Reader reader, PropertyModel property) throws IOException {
        if (!property.kind().shouldDeserialize()) {
            return null;
        }

        TypeRef<Object> type = resolveReadType(property);
        if (type == null) {
            return null;
        }

        TypeAdapter<Object> adapter = context.typeAdapters().getOrThrow(type);

        R rawValue = readRawValue(reader);
        if (rawValue == null) {
            return PropertyValue.Mutable.NOOP;
        }

        Reader valueReader = formatUtils.newReader(rawValue);
        ReadContext context = this.context.withKey(property.name());

        if (!(adapter instanceof TypeAdapter.Mutable)) {
            return parent -> adapter.read(
                    valueReader, context.withParent(parent)
            );
        }

        TypeAdapter.Mutable<Object> mutableAdapter = (TypeAdapter.Mutable<Object>) adapter;
        return (PropertyValue.Mutable<?>) (parent, instance) -> mutableAdapter.mutate(
                valueReader, instance, context.withParent(parent)
        );
    }

    private @Nullable R readRawValue(Reader reader) throws IOException {
        TypeRef<R> typeRef = TypeRef.of(formatUtils.rawType());
        TypeAdapter<R> adapter = context.typeAdapters().getOrThrow(typeRef);

        return adapter.read(reader, context.withParent(null).withKey(null));
    }

    private @Nullable TypeRef<Object> resolveReadType(PropertyModel property) {
        if (instance == null && property.parameter() != null) {
            return adapter.propertyTypes.parameterType(property);
        }

        return adapter.propertyTypes.readType(property);
    }
}
