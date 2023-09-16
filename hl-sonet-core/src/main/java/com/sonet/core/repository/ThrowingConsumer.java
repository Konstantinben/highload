package com.sonet.core.repository;

/**
 * @author konstantinp
 *
 * Copy of {@link java.util.function.Consumer} with throws Exception declaration
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
