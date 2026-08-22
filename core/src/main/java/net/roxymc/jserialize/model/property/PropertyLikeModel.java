package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface PropertyLikeModel {
    @Contract(pure = true)
    @Nullable String name();

    @Contract(pure = true)
    @Nullable PropertyMeta meta();

    @Contract(pure = true)
    default PropertyKind<?> kind() {
        PropertyMeta meta = meta();
        return meta != null ? meta.kind() : PropertyKind.PROPERTY;
    }
}
