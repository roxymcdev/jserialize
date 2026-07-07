package net.roxymc.jserialize.util;

import io.leangen.geantyref.AnnotatedCaptureType;
import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.model.property.MethodRef;
import net.roxymc.jserialize.type.TypeRef;

import java.lang.reflect.*;

import static io.leangen.geantyref.GenericTypeReflector.*;
import static java.lang.String.format;
import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static String getShortCanonicalName(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("clazz does not have a canonical name");
        }

        String packageName = clazz.getPackageName();
        if (packageName.isEmpty()) {
            return canonicalName;
        }

        return canonicalName.substring(packageName.length() + 1);
    }

    public static boolean isEnum(TypeRef<?> type) {
        return isEnum(type.getRawType());
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public static AnnotatedParameterizedType resolveTypeParams(TypeRef<?> type, Class<?> supertype) {
        return resolveTypeParams(type.getAnnotatedType(), supertype);
    }

    public static AnnotatedParameterizedType resolveTypeParams(AnnotatedType subtype, Class<?> supertype) {
        AnnotatedType type = getExactSuperType(capture(subtype), supertype);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(subtype.getType().getTypeName() + " must be a parameterized " + supertype.getTypeName());
        }

        return (AnnotatedParameterizedType) type;
    }

    public static AnnotatedType resolveUpperBound(AnnotatedType type) {
        if (type instanceof AnnotatedTypeVariable) {
            AnnotatedTypeVariable typeVar = (AnnotatedTypeVariable) type;

            return updateAnnotations(resolveUpperBound(typeVar.getAnnotatedBounds()[0]), type.getAnnotations());
        } else if (type instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType wtype = (AnnotatedWildcardType) type;

            checkLowerBounds(wtype.getAnnotatedLowerBounds());

            return updateAnnotations(resolveUpperBound(wtype.getAnnotatedUpperBounds()[0]), type.getAnnotations());
        } else if (type instanceof AnnotatedCaptureType) {
            AnnotatedCaptureType ctype = (AnnotatedCaptureType) type;

            checkLowerBounds(ctype.getAnnotatedLowerBounds());

            /*
            CaptureType can sometimes return [Object, MoreMeaningfulType] as upper bounds,
            that's why we need to skip any Object types and resolve to the first more meaningful type.

            A simple reproduction of this problem can be done with:
            `GenericTypeReflector.capture(new TypeRef<Collection<? extends Comparable<?>>>() {})`

            where the collection element type then resolves to:
            `capture of ? extends Comparable<?>
            for which upper bounds return [Object, Comparable<?>]
             */

            AnnotatedType[] upperBounds = ctype.getAnnotatedUpperBounds();

            AnnotatedType bound = upperBounds[0];
            for (int i = 1; i < upperBounds.length && bound.getType() == Object.class; i++) {
                bound = upperBounds[i];
            }

            return updateAnnotations(resolveUpperBound(bound), type.getAnnotations());
        }

        return type;
    }

    private static void checkLowerBounds(AnnotatedType[] lowerBounds) {
        if (lowerBounds.length == 0) {
            return;
        }

        throw new IllegalArgumentException(format(
                "Cannot resolve lower-bounded wildcard/capture of '? super %1$s' — use '%1$s' or '? extends %1$s' instead",
                GenericTypeReflector.getTypeName(lowerBounds[0].getType())
        ));
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, TypeRef<?> typeAndParams) {
        return resolveDirectType(unresolved, typeAndParams.getAnnotatedType());
    }

    public static AnnotatedType resolveDirectType(MethodRef unresolved, TypeRef<?> typeAndParams) {
        AnnotatedType declaringType = getExactSuperType(capture(typeAndParams.getAnnotatedType()), unresolved.declaringClass());

        return resolveDirectType(unresolved.valueType(), nonNull(declaringType, "declaringType"));
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
        if (unresolved instanceof AnnotatedWildcardType || unresolved instanceof AnnotatedCaptureType) {
            return resolveDirectType(resolveUpperBound(unresolved), typeAndParams);
        } else if (unresolved instanceof AnnotatedTypeVariable) {
            TypeVariable<?> typeVar = (TypeVariable<?>) unresolved.getType();
            if (!(typeVar.getGenericDeclaration() instanceof Class)) {
                return resolveUpperBound(unresolved);
            }

            @SuppressWarnings("unchecked")
            AnnotatedType type = GenericTypeReflector.getTypeParameter(typeAndParams, (TypeVariable<? extends Class<?>>) typeVar);
            if (type == null) {
                return resolveUpperBound(unresolved);
            }

            return resolveDirectType(updateAnnotations(type, unresolved.getAnnotations()), typeAndParams);
        } else {
            return GenericTypeReflector.resolveType(unresolved, typeAndParams);
        }
    }
}
