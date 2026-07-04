package net.roxymc.jserialize.util;

import io.leangen.geantyref.AnnotatedCaptureType;
import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.model.property.MethodRef;
import net.roxymc.jserialize.type.TypeRef;

import java.lang.reflect.*;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.getExactSuperType;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static boolean isEnum(TypeRef<?> type) {
        return isEnum(type.getRawType());
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public static AnnotatedParameterizedType resolveTypeParams(AnnotatedType subtype, Class<?> supertype) {
        AnnotatedType type = getExactSuperType(capture(subtype), supertype);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(subtype.getType().getTypeName() + " must be a parameterized " + supertype.getTypeName());
        }

        return (AnnotatedParameterizedType) type;
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, TypeRef<?> typeAndParams) {
        return resolveDirectType(unresolved, typeAndParams.getAnnotatedType());
    }

    public static AnnotatedType resolveDirectType(MethodRef unresolved, TypeRef<?> typeAndParams) {
        AnnotatedType declaringType = getExactSuperType(capture(typeAndParams.getAnnotatedType()), unresolved.declaringClass());

        return resolveDirectType(unresolved.valueType(), declaringType);
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
        if (unresolved instanceof AnnotatedWildcardType || unresolved instanceof AnnotatedCaptureType) {
            return resolveDirectType(GenericTypeReflector.reduceBounded(unresolved), typeAndParams);
        } else if (unresolved instanceof AnnotatedTypeVariable) {
            @SuppressWarnings("unchecked")
            TypeVariable<? extends Class<?>> typeVar = (TypeVariable<? extends Class<?>>) unresolved.getType();

            AnnotatedType type = GenericTypeReflector.getTypeParameter(typeAndParams, typeVar);
            if (type == null) {
                return unresolved;
            }

            return resolveDirectType(GenericTypeReflector.updateAnnotations(type, unresolved.getAnnotations()), typeAndParams);
        } else {
            return GenericTypeReflector.resolveType(unresolved, typeAndParams);
        }
    }
}
