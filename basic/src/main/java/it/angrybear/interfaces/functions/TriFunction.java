package it.angrybear.interfaces.functions;

import java.util.Objects;
import java.util.function.Function;

/**
 * Interface that represents a three parameters function.
 * (F, S, T) -> R
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <R> the return type
 */
@FunctionalInterface
public interface TriFunction<F, S, T, R> {

    /**
     * Apply function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @return the returning object
     */
    R apply(F first, S second, T third);

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a TriFunction that combines this and the function.
     */
    default <V> TriFunction<F, S, T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (f, s, t) -> after.apply(this.apply(f, s, t));
    }
}
