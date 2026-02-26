package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.model.property.meta.PropertyMeta;

import java.lang.reflect.Type;

public interface ParameterModel {
    String name();

    int index();

    Type type();

    boolean implicit();

    PropertyMeta meta();
}
