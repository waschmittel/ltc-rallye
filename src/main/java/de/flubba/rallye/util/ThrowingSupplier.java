package de.flubba.rallye.util;

@FunctionalInterface
public interface ThrowingSupplier<T, EXTYPE extends Throwable> {
    T get() throws EXTYPE;
}
