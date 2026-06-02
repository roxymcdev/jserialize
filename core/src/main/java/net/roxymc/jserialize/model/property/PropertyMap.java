package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PropertyMap extends Iterable<PropertyModel> {
    @ApiStatus.Internal
    static Builder builder() {
        return new PropertyMapImpl.BuilderImpl();
    }

    int size();

    @Nullable PropertyModel get(String name);

    @Nullable PropertyModel get(PropertyKind<?> kind);

    default Optional<PropertyModel> getOptional(PropertyKind<?> kind) {
        return Optional.ofNullable(get(kind));
    }

    @ApiStatus.NonExtendable
    interface Builder {
        Builder withProperty(String name, Consumer<PropertyModel.Builder> action);

        Builder withProperty(PropertyKind<?> kind, @Nullable String fallbackName, Consumer<PropertyModel.Builder> action);

        @ApiStatus.Internal
        PropertyMap build(MethodHandles.Lookup methodLookup) throws IllegalAccessException;
    }
}
