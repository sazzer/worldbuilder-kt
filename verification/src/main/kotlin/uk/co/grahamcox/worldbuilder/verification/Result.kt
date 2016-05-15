package uk.co.grahamcox.worldbuilder.verification

/**
 * Representation of the result of a call, which can be either successful or not
 */
sealed class Result<S, F> {
    /**
     * Representation of a successful call
     * @property value The value of the call
     */
    class Success<S, F>(val value: S) : Result<S, F>()

    /**
     * Representation of a unsuccessful call
     * @property value The value of the call
     */
    class Failure<S, F>(val value: F) : Result<S, F>()
}