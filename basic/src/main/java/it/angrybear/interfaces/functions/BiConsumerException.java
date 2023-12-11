package it.angrybear.interfaces.functions;

import java.util.Objects;

/**
 * Interface that represents a two parameters consumer that throws an exception.
 * (F, S) -> void
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 */
@FunctionalInterface
public interface BiConsumerException<F, S> {

    /**
     * Accept function.
     *
     * @param first  the first argument
     * @param second the second argument
     * @throws Exception the exception
     */
    void accept(F first, S second) throws Exception;

    /**
     * Apply this consumer and the after one.
     *
     * @param after the next consumer.
     * @return a consumer combining both this and the after one.
     */
    default BiConsumerException<F, S> andThen(BiConsumerException<? super F, ? super S> after) {
        Objects.requireNonNull(after);
        return (f, s) -> {
            this.accept(f, s);
            after.accept(f, s);
        };
    }
}
