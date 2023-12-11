package it.angrybear.interfaces.functions;

import java.util.Objects;
import java.util.function.Function;

/**
 * Interface that represents a two parameters function that throws an exception.
 * (F, S) -> R
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <R> the return type
 */
@FunctionalInterface
public interface BiFunctionException<F, S, R> {

    /**
     * Apply function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @return the returning object
     */
    R apply(F first, S second) throws Exception;

    /**
     * Apply this function and another function.
     *
     * @param <V>   the return type
     * @param after the after function
     * @return a TriFunction that combines this and the function.
     */
    default <V> BiFunctionException<F, S, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (f, s) -> after.apply(this.apply(f, s));
    }
}
