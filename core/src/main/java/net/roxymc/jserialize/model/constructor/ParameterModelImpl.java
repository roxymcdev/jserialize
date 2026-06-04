package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.util.ObjectUtils;
import net.roxymc.jserialize.util.TypeUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

final class ParameterModelImpl implements ParameterModel {
    private final String name;
    private final int index;
    private final AnnotatedType type;
    private final boolean implicit;
    private final PropertyMeta meta;

    ParameterModelImpl(String name, int index, Parameter parameter) {
        this.name = name;
        this.index = index;
        this.type = parameter.getAnnotatedType();

        Class<?> declaringClass = parameter.getDeclaringExecutable().getDeclaringClass();
        boolean implicitParent = index == 0 && parameter.isImplicit() && declaringClass.isMemberClass() && !Modifier.isStatic(declaringClass.getModifiers());

        this.implicit = implicitParent || (parameter.isImplicit() && !TypeUtils.isRecord(declaringClass));
        this.meta = implicitParent ? PropertyKind.PARENT.createMeta(null) : PropertyMeta.of(parameter);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public AnnotatedType type() {
        return type;
    }

    @Override
    public boolean implicit() {
        return implicit;
    }

    @Override
    public PropertyMeta meta() {
        return meta;
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this)
                .add("name='" + name + "'")
                .add("index=" + index)
                .add("type=" + type)
                .add("implicit=" + implicit)
                .add("meta=" + meta)
                .toString();
    }
}
