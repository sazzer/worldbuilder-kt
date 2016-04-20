package uk.co.grahamcox.worldbuilder.service.users

import java.time.Instant

/**
 * Representation of an actual User
 * @property id The ID of the User
 * @property created When the User was created
 * @property updated When the User was last updated
 * @property name The screen name of the User
 * @property email The email address of the User
 * @property enabled If the user account is enabled
 * @property verificationCode The verification code for the user account. Null if the user account is verified
 */
data class User(val id: UserId?,
                val created: Instant?,
                val updated: Instant?,
                val name: String,
                val email: String,
                val enabled: Boolean,
                val verificationCode: String?)