package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeWriter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.type.TypeRef;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

final class ObjectWriter<T, R> {
    private final ObjectAdapter<T> adapter;
    private final T instance;
    private final FormatUtils<R> formatUtils;
    private final WriteContext context;

    ObjectWriter(ObjectAdapter<T> adapter, T instance, FormatUtils<R> formatUtils, WriteContext context) {
        this.adapter = adapter;
        this.instance = instance;
        this.formatUtils = formatUtils;
        this.context = context;
    }

    void write(Writer writer) throws Throwable {
        writer.writeObjectStart();

        for (PropertyModel property : adapter.classModel.properties()) {
            try {
                writeProperty(writer, property);
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to write property: " + property.name(), ex);
            }
        }

        writer.writeObjectEnd();
    }

    private void writeProperty(Writer writer, PropertyModel property) throws Throwable {
        TypeRef<Object> type = adapter.propertyTypes.writeType(property);
        if (type == null) {
            return;
        }

        PropertyMeta meta = property.meta();

        Object value = requireNonNull(property.getter()).get(instance);
        if (value == null && meta != null && !meta.writeNull()) {
            return;
        }

        if (meta != null && meta.kind() == PropertyKind.EXTRAS) {
            // if it's an extras property, it never writes null
            requireNonNull(value);

            writeExtrasProperty(writer, type, value);
            return;
        }

        String name = resolveWriteName(property);
        writer.writeName(name);

        context.write(writer, type, value);
    }

    private void writeExtrasProperty(Writer writer, TypeRef<?> mapType, Object value) throws IOException {
        MapLike<R> extrasMap = formatUtils.createMap(context.typeAdapters(), mapType.getAnnotatedType());
        extrasMap.putAll((Map<?, ?>) value, context);

        TypeRef<R> rawType = TypeRef.of(formatUtils.rawType());
        TypeWriter<R> rawWriter = context.typeAdapters().getOrThrow(rawType);

        for (Map.Entry<String, R> entry : extrasMap.asRawMap().entrySet()) {
            writer.writeName(entry.getKey());

            rawWriter.write(writer, entry.getValue(), context);
        }
    }

    private String resolveWriteName(PropertyModel property) {
        String idPropertyName = formatUtils.idPropertyName();
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

        throw new IllegalStateException(format("'%s' property name is restricted for @Id property", idPropertyName));
    }
}
