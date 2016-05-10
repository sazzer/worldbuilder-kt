package uk.co.grahamcox.worldbuilder.webapp

/**
 * Details of a global error that occurred
 * @property errorCode The error code
 * @property message An error message
 */
data class GlobalError(val errorCode: String, val message: String?)

/**
 * Details of a field error that occurred
 * @property errorCode The error code
 * @property field The field the error occurred on
 * @property message An error message
 */
data class FieldError(val errorCode: String, val field: String, val message: String?)

/**
 * Exception to throw when an error occurred in a Data Fetcher
 * @property globalErrors Any global errors
 * @property fieldErrors Any field errors
 */
class FetcherException(val globalErrors: Collection<GlobalError> = listOf(),
                       val fieldErrors: Collection<FieldError> = listOf()) : Exception()