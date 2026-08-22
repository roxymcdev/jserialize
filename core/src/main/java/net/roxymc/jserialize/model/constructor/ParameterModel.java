package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.model.property.PropertyLikeModel;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.AnnotatedType;

@ApiStatus.NonExtendable
public interface ParameterModel extends PropertyLikeModel {
    @Contract(pure = true)
    int index();

    @Contract(pure = true)
    AnnotatedType type();

    @Contract(pure = true)
    boolean implicit();

    @Override
    PropertyMeta meta();
}
