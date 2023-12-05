package it.angrybear.interfaces.functions;

import java.util.Objects;

/**
 * Interface that represents a four parameters consumer.
 * (F, S, T, Q) -> void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @param <Q> the type parameter
 */
@FunctionalInterface
public interface QuadConsumer<F, S, T, Q> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @param third  the third argument
     * @param fourth the fourth argument
     */
    void accept(F first, S second, T third, Q fourth);

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default QuadConsumer<F, S, T, Q> andThen(QuadConsumer<? super F, ? super S, ? super T, ? super Q> after) {
        Objects.requireNonNull(after);
        return (f, s, t, q) -> {
            this.accept(f, s, t, q);
            after.accept(f, s, t, q);
        };
    }
}
