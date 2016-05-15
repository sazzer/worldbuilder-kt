package uk.co.grahamcox.worldbuilder.webapp

/**
 * Validator to ensure that the request is acceptable
 * @param <T> The type of request to validate
 */
interface RequestValidator<T> {
    /**
     * Validate the request
     * @param input The input request
     * @return the collection of errors. May be empty if there are none
     */
    fun validate(input: T) : Collection<Error>
}