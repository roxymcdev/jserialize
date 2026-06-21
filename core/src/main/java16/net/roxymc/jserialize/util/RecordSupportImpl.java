package net.roxymc.jserialize.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

final class RecordSupportImpl implements RecordUtils.RecordSupport {
    @Override
    public boolean isRecord(Class<?> clazz) {
        return clazz.isRecord();
    }

    @Override
    public boolean isPrimaryConstructor(Constructor<?> constructor) {
        Class<?> clazz = constructor.getDeclaringClass();
        if (!clazz.isRecord()) {
            return false;
        }

        RecordComponent[] components = clazz.getRecordComponents();
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        if (components.length != parameterTypes.length) {
            return false;
        }

        for (int i = 0; i < components.length; i++) {
            if (components[i].getType() != parameterTypes[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public AnnotatedType[] getComponentTypes(Class<?> clazz) {
        RecordComponent[] components = clazz.getRecordComponents();
        AnnotatedType[] types = new AnnotatedType[components.length];

        for (int i = 0; i < components.length; i++) {
            types[i] = components[i].getAnnotatedType();
        }

        return types;
    }
}
