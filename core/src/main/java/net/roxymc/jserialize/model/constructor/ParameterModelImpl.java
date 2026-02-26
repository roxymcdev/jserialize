package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.StringJoiner;

final class ParameterModelImpl implements ParameterModel {
    private final String name;
    private final int index;
    private final Type type;
    private final boolean implicit;
    private final PropertyMeta meta;

    ParameterModelImpl(String name, int index, Parameter parameter) {
        this.name = name;
        this.index = index;
        this.type = parameter.getParameterizedType();
        this.implicit = parameter.isImplicit();

        Class<?> declaringClass = parameter.getDeclaringExecutable().getDeclaringClass();
        boolean implicitParent = index == 0 && parameter.isImplicit() && declaringClass.isMemberClass() && !Modifier.isStatic(declaringClass.getModifiers());

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
    public Type type() {
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
        return new StringJoiner(", ", "ParameterModelImpl[", "]")
                .add("name=" + name)
                .add("index=" + index)
                .add("type=" + type)
                .add("implicit=" + implicit)
                .add("meta=" + meta)
                .toString();
    }
}
