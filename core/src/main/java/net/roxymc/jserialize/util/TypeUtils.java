package net.roxymc.jserialize.util;

import java.lang.reflect.*;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static Class<?> rawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            return Array.newInstance(rawType(genericArrayType.getGenericComponentType()), 0).getClass();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return rawType(parameterizedType.getRawType());
        } else if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            Type[] bounds = typeVariable.getBounds();
            return bounds.length > 0 ? rawType(bounds[0]) : Object.class;
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] lowerBounds = wildcardType.getLowerBounds();
            return rawType(lowerBounds.length > 0 ? lowerBounds[0] : wildcardType.getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
