package com.sonet.core.repository;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author konstantinp
 *
 * Copy of {@link Function} with throws Exception declaration
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

    R apply(T t) throws E;

    @SuppressWarnings("unused")
    default <V> ThrowingFunction<V, R, E> compose(ThrowingFunction<? super V, ? extends T, E> before)  {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    @SuppressWarnings("unused")
    default <V> ThrowingFunction<T, V, E> andThen(ThrowingFunction<? super R, ? extends V, E> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }
}
