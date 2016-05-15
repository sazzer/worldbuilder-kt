package uk.co.grahamcox.worldbuilder.verification.users

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

/**
 * Response object for user details
 * @property id The ID of the User
 * @property created When the user was created
 * @property updated When the user was updated
 * @property name The name of the user
 * @property email The email address of the user
 * @property enabled Whether the user is enabled
 * @property verified Whether the user is verified
 */
data class UserDetailsResponse @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("created") val created: Instant,
        @JsonProperty("updated") val updated: Instant,
        @JsonProperty("name") val name: String,
        @JsonProperty("email") val email: String?,
        @JsonProperty("enabled") val enabled: Boolean,
        @JsonProperty("verified") val verified: Boolean
)