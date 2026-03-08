package net.roxymc.jserialize.util;

public final class SneakyThrow {
    private SneakyThrow() {
    }

    @SuppressWarnings("unchecked")
    public static <X extends Throwable> RuntimeException sneakyThrow(Throwable ex) throws X {
        throw (X) ex;
    }
}
