package it.angrybear.interfaces.functions;

import java.util.Objects;

/**
 * Interface that represents a three parameters consumer.
 * (F, S, T) -> void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 */
@FunctionalInterface
public interface TriConsumer<F, S, T> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     */
    void apply(F first, S second, T third);

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default TriConsumer<F, S, T> andThen(TriConsumer<? super F, ? super S, ? super T> after) {
        Objects.requireNonNull(after);
        return (f, s, t) -> {
            this.apply(f, s, t);
            after.apply(f, s, t);
        };
    }
}
