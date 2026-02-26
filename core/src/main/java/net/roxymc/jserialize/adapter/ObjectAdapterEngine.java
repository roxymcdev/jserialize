package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.creator.InstanceCreator;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.GetterRef;
import net.roxymc.jserialize.model.property.MethodRef;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.util.Pair;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

import static java.lang.String.format;

public final class ObjectAdapterEngine<T, R> {
    private final ClassModel<T> classModel;
    private final Type type;
    private final FormatUtils<R> utils;

    public ObjectAdapterEngine(ClassModel<T> classModel, Type type, FormatUtils<R> utils) {
        this.classModel = classModel;
        this.type = type;
        this.utils = utils;
    }

    public T read(ReaderAdapter<R> reader, ReadContext<T> ctx) throws Throwable {
        reader.readStart();

        InstanceCreator.Builder<T> builder = InstanceCreator.builder(classModel)
                .parent(ctx.parent);

        PropertyModel extrasProperty = classModel.properties().get(PropertyKind.EXTRAS);
        MapLike<R> extrasMap = null;

        if (extrasProperty != null) {
            Type mapType = resolveReadType(extrasProperty, ctx);

            extrasMap = mapType != null ? utils.createMap(mapType) : null;
        }

        for (Pair<String, @Nullable PropertyModel> pair : reader.properties()) {
            PropertyModel property = pair.second();

            if (property != null) {
                PropertyValue<?> value = readProperty(reader, property, ctx);

                if (value != null) {
                    builder.property(property.name(), value);
                    continue;
                }
            }

            if (extrasMap != null) {
                String name = pair.first();
                // even tho marked as non-null, the ide thinks it's nullable...
                assert name != null;

                extrasMap.put(name, reader.readRawValue(name));
                continue;
            }

            reader.skipValue();
        }

        reader.readEnd();

        if (extrasMap != null) {
            builder.property(extrasProperty.name(), extrasMap.asMap());
        }

        InstanceCreator<T> creator = builder.build();
        return ctx.instance == null ? creator.createInstance() : creator.populate(ctx.instance);
    }

    private @Nullable PropertyValue<?> readProperty(ReaderAdapter<R> reader, PropertyModel property, ReadContext<T> ctx) throws Throwable {
        PropertyMeta meta = property.meta();
        if (meta != null && !meta.shouldDeserialize()) {
            return null;
        }

        Type propertyType = resolveReadType(property, ctx);
        if (propertyType == null) {
            return null;
        }

        return reader.readValue(property.name(), propertyType);
    }

    private @Nullable Type resolveReadType(PropertyModel property, ReadContext<?> ctx) {
        PropertyMeta meta = property.meta();

        if (ctx.instance == null && property.parameterType() != null) {
            return GenericTypeReflector.resolveType(property.parameterType(), type);
        }

        MethodRef ref = null;

        if (meta != null && meta.mutate() && property.getter() != null) {
            ref = property.getter();
        } else if (property.setter() != null) {
            ref = property.setter();
        }

        if (ref == null) {
            return null;
        }

        Type supertype = GenericTypeReflector.getExactSuperType(type, ref.declaringClass());
        return GenericTypeReflector.resolveType(ref.valueType(), supertype);
    }

    public void write(WriterAdapter writer, T instance) throws Throwable {
        writer.writeStart();

        for (PropertyModel property : classModel.properties().values()) {
            writeProperty(writer, property, instance);
        }

        writer.writeEnd();
    }

    private void writeProperty(WriterAdapter writer, @Nullable PropertyModel property, Object instance) throws Throwable {
        if (property == null) {
            return;
        }

        PropertyMeta meta = property.meta();
        GetterRef getter = property.getter();

        if (meta != null && !meta.shouldSerialize() || getter == null) {
            return;
        }

        Object value = getter.get(instance);
        if (value == null && meta != null && !meta.writeNull()) {
            return;
        }

        Type supertype = GenericTypeReflector.getExactSuperType(type, getter.declaringClass());
        Type propertyType = GenericTypeReflector.resolveType(getter.valueType(), supertype);

        if (meta != null && meta.kind() == PropertyKind.EXTRAS) {
            MapLike<R> extrasMap = utils.createMap(propertyType);

            // if it's an extras property, it never writes null
            assert value != null;
            extrasMap.putAll((Map<?, ?>) value);

            for (Map.Entry<String, R> entry : extrasMap.asRawMap().entrySet()) {
                writer.writeProperty(entry.getKey(), utils.rawType(), entry.getValue());
            }
            return;
        }

        String name = resolveWriteName(property);

        writer.writeProperty(name, propertyType, value);
    }

    private String resolveWriteName(PropertyModel property) {
        String idPropertyName = utils.idPropertyName();
        if (idPropertyName == null) {
            return property.name();
        }

        if (property.kind() == PropertyKind.ID) {
            return idPropertyName;
        }

        String name = property.name();
        if (!idPropertyName.equals(name)) {
            return name;
        }

        throw new IllegalStateException(format(
                "'%s' property name is restricted for @Id property",
                idPropertyName
        ));
    }
}
