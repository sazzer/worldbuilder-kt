package uk.co.grahamcox.worldbuilder.webapp

/**
 * Response payload representing an error that has occurred
 * @property globalErrors The list of global errors
 * @property fieldErrors The list of field errors
 */
data class ErrorResponseModel(val globalErrors: Collection<GlobalError>,
                              val fieldErrors: Collection<FieldError>)