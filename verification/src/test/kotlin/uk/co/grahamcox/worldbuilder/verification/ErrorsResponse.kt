package uk.co.grahamcox.worldbuilder.verification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of a global error
 * @property errorCode The error code
 */
data class GlobalError @JsonCreator constructor(
        @JsonProperty("errorCode") val errorCode: String
)

/**
 * Representation of a field error
 * @property errorCode The error code
 * @property field The field
 */
data class FieldError @JsonCreator constructor(
        @JsonProperty("errorCode") val errorCode: String,
        @JsonProperty("field") val field: String
)

/**
 * Representation of an errors response
 * @property globalErrors The global errors
 * @property fieldErrors The field errors
 */
data class ErrorsResponse @JsonCreator constructor(
        @JsonProperty("globalErrors") val globalErrors: List<GlobalError>,
        @JsonProperty("fieldErrors") val fieldErrors: List<FieldError>
)