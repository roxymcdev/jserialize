package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PropertyMap {
    @ApiStatus.Internal
    static Builder builder() {
        return new PropertyMapImpl.BuilderImpl();
    }

    int size();

    Iterable<PropertyModel> values();

    @Nullable PropertyModel get(String name);

    @Nullable PropertyModel get(PropertyKind<?> kind);

    @ApiStatus.NonExtendable
    interface Builder {
        Builder withProperty(String name, Consumer<PropertyModel.Builder> action);

        Builder withProperty(PropertyKind<?> kind, String fallbackName, Consumer<PropertyModel.Builder> action);

        @ApiStatus.Internal
        PropertyMap build(MethodHandles.Lookup methodLookup) throws IllegalAccessException;
    }
}
