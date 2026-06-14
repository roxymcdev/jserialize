package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.GetterRef;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.type.TypeToken;
import net.roxymc.jserialize.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Objects;

import static io.leangen.geantyref.GenericTypeReflector.*;
import static java.lang.String.format;

final class ObjectWriter<T, R> {
    private final ClassModel<T> classModel;
    private final TypeToken<? extends T> type;
    private final T instance;
    private final FormatUtils<R> formatUtils;
    private final WriteContext context;

    ObjectWriter(
            ClassModel<T> classModel,
            TypeToken<? extends T> type,
            T instance,
            FormatUtils<R> formatUtils,
            WriteContext context
    ) {
        this.classModel = classModel;
        this.type = type;
        this.instance = instance;
        this.formatUtils = formatUtils;
        this.context = context;
    }

    void write(Writer writer) throws Throwable {
        writer.writeObjectStart();

        for (PropertyModel property : classModel.properties()) {
            try {
                writeProperty(writer, property);
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to write property: " + property.name(), ex);
            }
        }

        writer.writeObjectEnd();
    }

    private void writeProperty(Writer writer, PropertyModel property) throws Throwable {
        PropertyMeta meta = property.meta();
        GetterRef getter = property.getter();

        if (meta != null && !meta.kind().shouldSerialize() || getter == null) {
            return;
        }

        Object value = getter.get(instance);
        if (value == null && meta != null && !meta.writeNull()) {
            return;
        }

        AnnotatedType declaringType = getExactSuperType(capture(type.getAnnotatedType()), getter.declaringClass());
        AnnotatedType type = TypeUtils.box(resolveType(getter.valueType(), declaringType));

        if (meta != null && meta.kind() == PropertyKind.EXTRAS) {
            // if it's an extras property, it never writes null
            Objects.requireNonNull(value);

            writeExtrasProperty(writer, type, value);
            return;
        }

        TypeToken<Object> typeToken = TypeToken.of(type);
        TypeAdapter<Object> adapter = context.typeAdapters().getOrThrow(typeToken);

        String name = resolveWriteName(property);
        writer.writeName(name);
        adapter.write(writer, typeToken, value, context);
    }

    private void writeExtrasProperty(Writer writer, AnnotatedType type, Object value) throws IOException {
        MapLike<R> extrasMap = formatUtils.createMap(context.typeAdapters(), type);
        extrasMap.putAll((Map<?, ?>) value, context);

        TypeToken<R> typeToken = TypeToken.of(formatUtils.rawType());
        TypeAdapter<R> adapter = context.typeAdapters().getOrThrow(typeToken);

        for (Map.Entry<String, R> entry : extrasMap.asRawMap().entrySet()) {
            writer.writeName(entry.getKey());
            adapter.write(writer, typeToken, entry.getValue(), context);
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

        throw new IllegalStateException(format(
                "'%s' property name is restricted for @Id property",
                idPropertyName
        ));
    }
}
