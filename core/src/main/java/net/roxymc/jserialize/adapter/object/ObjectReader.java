package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.creator.InstanceCreator;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.MethodRef;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.type.TypeToken;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;

import static io.leangen.geantyref.GenericTypeReflector.*;

final class ObjectReader<T, R> {
    private final ClassModel<T> classModel;
    private final TypeToken<? extends T> type;
    private final @Nullable T instance;
    private final FormatUtils<R> formatUtils;
    private final ReadContext context;

    ObjectReader(
            ClassModel<T> classModel,
            TypeToken<? extends T> type,
            @Nullable T instance,
            FormatUtils<R> formatUtils,
            ReadContext context
    ) {
        this.classModel = classModel;
        this.type = type;
        this.instance = instance;
        this.formatUtils = formatUtils;
        this.context = context;
    }

    T read(Reader reader) throws Throwable {
        reader.readObjectStart();

        InstanceCreator.Builder<T> builder = InstanceCreator.builder(classModel);

        classModel.properties().getOptional(PropertyKind.PARENT).ifPresent(property ->
                builder.property(property, context.parent())
        );
        classModel.properties().getOptional(PropertyKind.KEY).ifPresent(property ->
                // TODO we should pass the key through the desired key adapter
                builder.property(property, context.key())
        );

        PropertyModel extrasProperty = classModel.properties().get(PropertyKind.EXTRAS);
        MapLike<R> extrasMap;

        if (extrasProperty != null) {
            AnnotatedType mapType = resolveReadType(extrasProperty);

            extrasMap = mapType != null ? formatUtils.createMap(context.typeAdapters(), mapType) : null;
        } else {
            extrasMap = null;
        }

        while (reader.peek() == TokenType.NAME) {
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
            builder.property(extrasProperty, parent -> extrasMap.asMap(
                    context.withParent(parent).withKey(extrasProperty.name())
            ));
        }

        InstanceCreator<T> creator = builder.build();
        return instance == null ? creator.createInstance() : creator.populate(instance);
    }

    private @Nullable PropertyModel resolveProperty(String name) {
        String idPropertyName = formatUtils.idPropertyName();
        if (idPropertyName == null) {
            return classModel.properties().get(name);
        }

        if (name.equals(idPropertyName)) {
            return classModel.properties().get(PropertyKind.ID);
        }

        PropertyModel property = classModel.properties().get(name);
        return property != null && property.kind() != PropertyKind.ID ? property : null;
    }

    private @Nullable PropertyValue<?> readProperty(Reader reader, PropertyModel property) throws IOException {
        PropertyMeta meta = property.meta();
        if (meta != null && !meta.kind().shouldDeserialize()) {
            return null;
        }

        AnnotatedType type = resolveReadType(property);
        if (type == null) {
            return null;
        }

        TypeToken<Object> typeToken = TypeToken.of(type);
        TypeAdapter<Object> adapter = context.typeAdapters().getOrThrow(typeToken);

        R rawValue = readRawValue(reader);
        if (rawValue == null) {
            return PropertyValue.Mutable.NOOP;
        }

        Reader valueReader = formatUtils.newReader(rawValue);
        ReadContext context = this.context.withKey(property.name());

        if (!(adapter instanceof TypeAdapter.Mutable)) {
            return parent -> adapter.read(
                    valueReader, typeToken, context.withParent(parent)
            );
        }

        TypeAdapter.Mutable<Object> mutableAdapter = (TypeAdapter.Mutable<Object>) adapter;
        return (PropertyValue.Mutable<?>) (parent, instance) -> mutableAdapter.mutate(
                valueReader, typeToken, instance, context.withParent(parent)
        );
    }

    private @Nullable R readRawValue(Reader reader) throws IOException {
        TypeToken<R> typeToken = TypeToken.of(formatUtils.rawType());
        TypeAdapter<R> adapter = context.typeAdapters().getOrThrow(typeToken);

        return adapter.read(reader, typeToken, context.withParent(null).withKey(null));
    }

    private @Nullable AnnotatedType resolveReadType(PropertyModel property) {
        PropertyMeta meta = property.meta();

        if (instance == null && property.parameterType() != null) {
            return TypeUtils.box(resolveType(property.parameterType(), capture(type.getAnnotatedType())));
        }

        MethodRef method = null;

        if (meta != null && meta.mutate() && property.getter() != null) {
            method = property.getter();
        } else if (property.setter() != null) {
            method = property.setter();
        }

        if (method == null) {
            return null;
        }

        AnnotatedType supertype = getExactSuperType(capture(type.getAnnotatedType()), method.declaringClass());
        return TypeUtils.box(resolveType(method.valueType(), supertype));
    }
}
