package uk.co.grahamcox.worldbuilder.webapp.users

import java.time.Instant

/**
 * Representation of a User
 * @property id The ID of the user
 * @property created When the user was created
 * @property updated When the user was last updated
 * @property name The name of the user
 * @property email The email address of the user
 * @property enabled If the user is enabled
 * @property verified If the user account is verified
 */
data class UserModel(val id: String,
                     val created: Instant,
                     val updated: Instant,
                     val name: String,
                     val email: String?,
                     @get:JvmName("isEnabled") val enabled: Boolean,
                     @get:JvmName("isVerified") val verified: Boolean)