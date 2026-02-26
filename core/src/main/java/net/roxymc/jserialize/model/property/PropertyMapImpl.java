package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;

final class PropertyMapImpl implements PropertyMap {
    private final Map<String, PropertyModel> nameToProperty;
    private final Map<PropertyKind<?>, PropertyModel> kindToProperty;

    PropertyMapImpl(BuilderImpl builder) throws IllegalAccessException {
        Map<String, PropertyModel> nameToProperty = new HashMap<>();

        for (Map.Entry<String, PropertyModelImpl.BuilderImpl> entry : builder.nameToProperty.entrySet()) {
            String name = entry.getKey();
            PropertyModel property = entry.getValue().build();

            nameToProperty.put(name, property);
        }

        this.nameToProperty = Collections.unmodifiableMap(nameToProperty);
        this.kindToProperty = builder.kindToName.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> nameToProperty.get(entry.getValue())
        ));
    }

    @Override
    public int size() {
        return nameToProperty.size();
    }

    @Override
    public Iterable<PropertyModel> values() {
        return nameToProperty.values();
    }

    @Override
    public @Nullable PropertyModel get(String name) {
        return nameToProperty.get(name);
    }

    @Override
    public @Nullable PropertyModel get(PropertyKind<?> kind) {
        return kindToProperty.get(kind);
    }

    static final class BuilderImpl implements Builder {
        private final Map<String, PropertyModelImpl.BuilderImpl> nameToProperty = new HashMap<>();
        private final Map<PropertyKind<?>, String> kindToName = new HashMap<>();

        @Override
        public Builder withProperty(String name, Consumer<PropertyModel.Builder> action) {
            PropertyModelImpl.BuilderImpl builder = nameToProperty.computeIfAbsent(name, PropertyModelImpl.BuilderImpl::new);
            action.accept(builder);

            if (builder.kind != null && builder.kind != PropertyKind.PROPERTY) {
                String previousName = kindToName.putIfAbsent(builder.kind, name);

                if (previousName != null && !previousName.equals(name)) {
                    throw new IllegalStateException(format(
                            "Multiple properties of kind %s found",
                            builder.kind
                    ));
                }
            }

            return this;
        }

        @Override
        public Builder withProperty(PropertyKind<?> kind, String fallbackName, Consumer<PropertyModel.Builder> action) {
            String name = kindToName.getOrDefault(kind, fallbackName);

            PropertyModelImpl.BuilderImpl builder = nameToProperty.computeIfAbsent(name, PropertyModelImpl.BuilderImpl::new);
            if (builder.kind != null && builder.kind != kind) {
                throw new IllegalStateException(format(
                        "Expected kind: %s, but found: %s",
                        kind, builder.kind
                ));
            }

            builder.kind = kind;

            return withProperty(name, action);
        }

        @Override
        public PropertyMap build() throws IllegalAccessException {
            return new PropertyMapImpl(this);
        }
    }
}
