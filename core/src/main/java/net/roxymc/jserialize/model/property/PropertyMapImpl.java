package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

final class PropertyMapImpl implements PropertyMap {
    private final Map<String, PropertyModel> properties;
    private final @Nullable PropertyModel idProperty;
    private final @Nullable PropertyModel extrasProperty;

    PropertyMapImpl(BuilderImpl builder) throws IllegalAccessException {
        Map<String, PropertyModel> propertyMap = new HashMap<>();
        PropertyModel idProperty = null;
        PropertyModel extrasProperty = null;

        for (Map.Entry<String, PropertyModel.Builder> entry : builder.properties.entrySet()) {
            String name = entry.getKey();
            PropertyModel property = entry.getValue().build();

            PropertyMeta meta = property.meta();
            if (meta != null) {
                if (meta.id()) {
                    if (idProperty != null) {
                        throw new IllegalStateException("Multiple @Id properties found");
                    }

                    idProperty = property;
                    continue;
                }

                if (meta.extra()) {
                    if (extrasProperty != null) {
                        throw new IllegalStateException("Multiple @ExtraProperties properties found");
                    }

                    extrasProperty = property;
                    continue;
                }
            }

            propertyMap.put(name, property);
        }

        Map<String, PropertyModel> orderedProperties = new LinkedHashMap<>();
        if (idProperty != null) {
            orderedProperties.put(idProperty.name(), idProperty);
        }
        orderedProperties.putAll(propertyMap);
        if (extrasProperty != null) {
            orderedProperties.put(extrasProperty.name(), extrasProperty);
        }

        this.properties = Collections.unmodifiableMap(orderedProperties);
        this.idProperty = idProperty;
        this.extrasProperty = extrasProperty;
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public Iterable<PropertyModel> values() {
        return properties.values();
    }

    @Override
    public @Nullable PropertyModel get(String name, boolean excludeId) {
        PropertyModel property = properties.get(name);

        if (excludeId && property == idProperty) {
            return null;
        }

        return property;
    }

    @Override
    public @Nullable PropertyModel idProperty() {
        return idProperty;
    }

    @Override
    public @Nullable PropertyModel extrasProperty() {
        return extrasProperty;
    }

    static final class BuilderImpl implements Builder {
        private final Map<String, PropertyModel.Builder> properties = new HashMap<>();

        @Override
        public Builder withProperty(String name, Consumer<PropertyModel.Builder> builder) {
            builder.accept(properties.computeIfAbsent(name, PropertyModel::builder));
            return this;
        }

        @Override
        public PropertyMap build() throws IllegalAccessException {
            return new PropertyMapImpl(this);
        }
    }
}
