package net.roxymc.jserialize.model.property;

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
    private final Map<@Nullable String, PropertyModel> nameToProperty;
    private final Map<PropertyKind<?>, PropertyModel> kindToProperty;

    PropertyMapImpl(BuilderImpl builder, MethodHandles.Lookup methodLookup) throws IllegalAccessException {
        Map<@Nullable String, PropertyModel> nameToProperty = new LinkedHashMap<>();

        for (Map.Entry<@Nullable String, PropertyModelImpl.BuilderImpl> entry : builder.nameToProperty.entrySet()) {
            String name = entry.getKey();
            PropertyModel property = entry.getValue().build(methodLookup);

            nameToProperty.put(name, property);
        }

        this.nameToProperty = Collections.unmodifiableMap(nameToProperty);
        this.kindToProperty = builder.kindToName.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> this.nameToProperty.get(entry.getValue())
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
    public @Nullable PropertyModel get(PropertyKind<?> kind) {
        return kindToProperty.get(kind);
    }

    @Override
    public Iterator<PropertyModel> iterator() {
        return nameToProperty.values().iterator();
    }

    @Override
    public String toString() {
        return nameToProperty.toString();
    }

    static final class BuilderImpl implements Builder {
        private final Map<@Nullable String, PropertyModelImpl.BuilderImpl> nameToProperty = new LinkedHashMap<>();
        private final Map<PropertyKind<?>, @Nullable String> kindToName = new HashMap<>();

        @Override
        public Builder withProperty(String name, Consumer<PropertyModel.Builder> action) {
            return withProperty0(nonNull(name, "name"), nonNull(action, "action"));
        }

        @Override
        public Builder withProperty(PropertyKind<?> kind, @Nullable String fallbackName, Consumer<PropertyModel.Builder> action) {
            nonNull(kind, "kind");
            nonNull(action, "action");

            String name = kindToName.getOrDefault(kind, fallbackName);
            if (name == null && (kind.shouldSerialize() || kind.shouldDeserialize())) {
                throw new IllegalStateException("Serializable property must have a name");
            }

            PropertyModelImpl.BuilderImpl builder = nameToProperty.computeIfAbsent(name, $ ->
                    new PropertyModelImpl.BuilderImpl(nonNullOrElse(name, "<implicit>"))
            );
            builder.checkKind(kind);

            return withProperty0(name, action);
        }

        private Builder withProperty0(@Nullable String name, Consumer<PropertyModel.Builder> action) {
            PropertyModelImpl.BuilderImpl builder = nameToProperty.computeIfAbsent(name, $ ->
                    new PropertyModelImpl.BuilderImpl(nonNull(name, "name"))
            );
            action.accept(builder);

            if (builder.kind != null && builder.kind != PropertyKind.PROPERTY) {
                String previousName = kindToName.get(builder.kind);

                if (kindToName.containsKey(builder.kind) && !Objects.equals(previousName, name)) {
                    throw new IllegalStateException(format(
                            "Multiple properties of kind %s found: %s, %s",
                            builder.kind, nameToProperty.get(previousName).name, builder.name
                    ));
                }

                kindToName.put(builder.kind, name);
            }

            return this;
        }

        @Override
        public PropertyMap build(MethodHandles.Lookup methodLookup) throws IllegalAccessException {
            return new PropertyMapImpl(this, methodLookup);
        }
    }
}
