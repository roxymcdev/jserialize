package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.ObjectUtils.nonNullOrElse;

final class PropertyMapImpl implements PropertyMap {
    private final Set<PropertyModel> properties;
    private final Map<String, PropertyModel> nameToProperty;
    private final Map<ParameterModel, PropertyModel> parameterToProperty;
    private final Map<PropertyKind<?>, PropertyModel> kindToProperty;

    PropertyMapImpl(BuilderImpl builder, MethodHandles.Lookup methodLookup) throws IllegalAccessException {
        Map<Integer, PropertyModel> properties = new LinkedHashMap<>(builder.properties.size());

        for (Map.Entry<Integer, PropertyModelImpl.BuilderImpl> entry : builder.properties.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().build(methodLookup));
        }

        this.properties = Collections.unmodifiableSet(new LinkedHashSet<>(properties.values()));

        this.nameToProperty = builder.nameToId.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> properties.get(entry.getValue())
        ));
        this.parameterToProperty = builder.parameterToId.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> properties.get(entry.getValue())
        ));
        this.kindToProperty = builder.kindToId.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> properties.get(entry.getValue())
        ));
    }

    @Override
    public int size() {
        return nameToProperty.size();
    }

    @Override
    public @Nullable PropertyModel get(String name) {
        return nameToProperty.get(name);
    }

    @Override
    public @Nullable PropertyModel get(ParameterModel parameter) {
        return parameterToProperty.get(parameter);
    }

    @Override
    public @Nullable PropertyModel get(PropertyKind<?> kind) {
        return kindToProperty.get(kind);
    }

    @Override
    public Iterator<PropertyModel> iterator() {
        return properties.iterator();
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    static final class BuilderImpl implements Builder {
        private final Map<Integer, PropertyModelImpl.BuilderImpl> properties = new LinkedHashMap<>();
        private final Map<String, Integer> nameToId = new HashMap<>();
        private final Map<ParameterModel, Integer> parameterToId = new HashMap<>();
        private final Map<PropertyKind<?>, Integer> kindToId = new HashMap<>();

        @Override
        public Builder withProperty(String name, Consumer<? super PropertyModel.Builder> action) {
            nonNull(name, "name");
            nonNull(action, "action");

            int id = nameToId.getOrDefault(name, properties.size());
            return withProperty0(id, name, action);
        }

        @Override
        public Builder withProperty(PropertyKind<?> kind, @Nullable String fallbackName, Consumer<? super PropertyModel.Builder> action) {
            nonNull(kind, "kind");
            nonNull(action, "action");

            int id = nonNullOrElse(kindToId.get(kind), () -> nonNullOrElse(nameToId.get(fallbackName), properties.size()));

            PropertyModelImpl.BuilderImpl builder = properties.compute(id, ($, builder0) -> {
                String name = builder0 != null ? builder0.name : fallbackName;

                if (name == null && kind.shouldDeserialize()) {
                    throw new IllegalStateException("Deserializable property must have a name");
                }

                if (builder0 == null) {
                    builder0 = new PropertyModelImpl.BuilderImpl(name);
                }

                builder0.checkKind(kind);
                return builder0;
            });

            return withProperty0(id, builder.name, action);
        }

        private Builder withProperty0(int id, @Nullable String name, Consumer<? super PropertyModel.Builder> action) {
            if (name != null && nonNullOrElse(nameToId.get(name), id) != id) {
                throw new IllegalStateException(format("Property already exists: %s", name));
            }

            PropertyModelImpl.BuilderImpl builder = properties.compute(id, ($, builder0) -> {
                if (builder0 == null) {
                    builder0 = new PropertyModelImpl.BuilderImpl(nonNull(name, "name"));
                }

                action.accept(builder0);
                return builder0;
            });

            if (name != null) {
                nameToId.put(name, id);
            }

            if (builder.parameter != null) {
                parameterToId.compute(builder.parameter, (parameter, boundId) -> {
                    if (boundId == null || boundId == id) {
                        return id;
                    }

                    throw new IllegalStateException(format(
                            "Multiple properties of parameter %s found: %s, %s",
                            parameter, properties.get(boundId).name, builder.name
                    ));
                });
            }

            if (builder.kind != null && builder.kind != PropertyKind.PROPERTY) {
                kindToId.compute(builder.kind, (kind, boundId) -> {
                    if (boundId == null || boundId == id) {
                        return id;
                    }

                    throw new IllegalStateException(format(
                            "Multiple properties of kind %s found: %s, %s",
                            kind, properties.get(boundId).name, builder.name
                    ));
                });
            }

            return this;
        }

        @Override
        public PropertyMap build(MethodHandles.Lookup methodLookup) throws IllegalAccessException {
            return new PropertyMapImpl(this, methodLookup);
        }
    }
}
