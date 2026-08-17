package net.roxymc.jserialize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class JSerial {
    private JSerial() {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface Creator {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Extra {
        boolean DEFAULT_MUTATE = false;

        String value() default "";

        /**
         * works only on getter
         */
        boolean mutate() default DEFAULT_MUTATE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Id {
        boolean DEFAULT_REQUIRED = true;

        String value() default "";

        boolean required() default DEFAULT_REQUIRED;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Key {
        boolean DEFAULT_REQUIRED = true;

        String value() default "";

        boolean required() default DEFAULT_REQUIRED;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Parent {
        boolean DEFAULT_REQUIRED = true;

        String value() default "";

        boolean required() default DEFAULT_REQUIRED;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Property {
        boolean DEFAULT_REQUIRED = false;
        boolean DEFAULT_WRITE_NULL = false;
        boolean DEFAULT_MUTATE = false;

        String value() default "";

        /**
         * works only on getter/parameter
         */
        boolean required() default DEFAULT_REQUIRED;

        /**
         * works only on getter
         */
        boolean writeNull() default DEFAULT_WRITE_NULL;

        /**
         * works only on getter
         */
        boolean mutate() default DEFAULT_MUTATE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface Transient {
    }
}
