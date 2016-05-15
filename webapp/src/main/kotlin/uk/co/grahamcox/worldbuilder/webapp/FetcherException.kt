package uk.co.grahamcox.worldbuilder.webapp

/**
 * Interface for an error
 */
interface Error {
    /** The error code */
    val errorCode: String
    /** The error message */
    val message: String?
}

/**
 * Details of a global error that occurred
 * @property errorCode The error code
 * @property message An error message
 */
data class GlobalError(override val errorCode: String, override val message: String?) : Error

/**
 * Details of a field error that occurred
 * @property errorCode The error code
 * @property field The field the error occurred on
 * @property message An error message
 */
data class FieldError(override val errorCode: String, val field: String, override val message: String?) : Error

/**
 * Exception to throw when an error occurred in a Data Fetcher
 * @property globalErrors Any global errors
 * @property fieldErrors Any field errors
 */
class FetcherException(val globalErrors: Collection<GlobalError> = listOf(),
                       val fieldErrors: Collection<FieldError> = listOf()) : Exception()