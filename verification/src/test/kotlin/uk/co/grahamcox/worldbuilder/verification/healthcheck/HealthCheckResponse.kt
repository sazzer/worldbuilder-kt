package uk.co.grahamcox.worldbuilder.verification.healthcheck

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of a Health Check response
 * @property name The name of the healthcheck
 * @property status The status of the healthcheck
 * @property message The message from the healthcheck
 */
data class HealthCheckResponse @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("status") val status: String,
        @JsonProperty("message") val message: String?
)