package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.model.property.meta.PropertyMeta;

import java.lang.reflect.AnnotatedType;

public interface ParameterModel {
    String name();

    int index();

    AnnotatedType type();

    boolean implicit();

    PropertyMeta meta();
}
