package net.roxymc.jserialize.adapter.object;

import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;

import static net.roxymc.jserialize.util.TypeUtils.resolveDirectType;

final class PropertyTypes {
    private final Map<PropertyModel, TypeRef<?>> parameterTypes;
    private final Map<PropertyModel, TypeRef<?>> readTypes;
    private final Map<PropertyModel, TypeRef<?>> writeTypes;

    <T> PropertyTypes(TypeRef<T> type, ClassModel<T> classModel) {
        Map<PropertyModel, TypeRef<?>> parameterTypes = new HashMap<>();
        Map<PropertyModel, TypeRef<?>> readTypes = new HashMap<>();
        Map<PropertyModel, TypeRef<?>> writeTypes = new HashMap<>();

        for (PropertyModel property : classModel.properties()) {
            PropertyMeta meta = property.meta();

            if (property.parameterType() != null) {
                AnnotatedType resolvedType = resolveDirectType(property.parameterType(), type);

                parameterTypes.put(property, TypeRef.of(resolvedType));
            }

            if (property.getter() != null) {
                AnnotatedType resolvedType = resolveDirectType(property.getter(), type);

                if (meta != null && meta.mutate()) {
                    readTypes.put(property, TypeRef.of(resolvedType));
                }

                // we check for shouldSerialize here, because all non-serializable properties are skipped
                if (property.kind().shouldSerialize()) {
                    writeTypes.put(property, TypeRef.of(resolvedType));
                }
            }

            // we don't check for shouldDeserialize here, because non-deserializable properties are still resolved
            if (!readTypes.containsKey(property) && property.setter() != null) {
                AnnotatedType resolvedType = resolveDirectType(property.setter(), type);

                readTypes.put(property, TypeRef.of(resolvedType));
            }
        }

        this.parameterTypes = Map.copyOf(parameterTypes);
        this.readTypes = Map.copyOf(readTypes);
        this.writeTypes = Map.copyOf(writeTypes);
    }

    public <T> @Nullable TypeRef<T> parameterType(PropertyModel property) {
        @SuppressWarnings("unchecked")
        TypeRef<T> type = (TypeRef<T>) parameterTypes.get(property);
        return type;
    }

    public <T> @Nullable TypeRef<T> readType(PropertyModel property) {
        @SuppressWarnings("unchecked")
        TypeRef<T> type = (TypeRef<T>) readTypes.get(property);
        return type;
    }

    public <T> @Nullable TypeRef<T> writeType(PropertyModel property) {
        @SuppressWarnings("unchecked")
        TypeRef<T> type = (TypeRef<T>) writeTypes.get(property);
        return type;
    }
}
