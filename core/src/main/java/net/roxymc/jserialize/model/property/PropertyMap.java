package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PropertyMap {
    static Builder builder() {
        return new PropertyMapImpl.BuilderImpl();
    }

    int size();

    Iterable<PropertyModel> values();

    default @Nullable PropertyModel get(String name) {
        return get(name, false);
    }

    @Nullable PropertyModel get(String name, boolean excludeId);

    @Nullable PropertyModel idProperty();

    @Nullable PropertyModel extrasProperty();

    @ApiStatus.NonExtendable
    interface Builder {
        Builder withProperty(String name, Consumer<PropertyModel.Builder> builder);

        PropertyMap build() throws IllegalAccessException;
    }
}
