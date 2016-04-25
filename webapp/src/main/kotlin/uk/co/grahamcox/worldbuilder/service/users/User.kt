package uk.co.grahamcox.worldbuilder.service.users

import uk.co.grahamcox.worldbuilder.service.Identity

/**
 * Representation of a User record
 * @property identity The identity of the user
 * @property name The name of the user
 * @property email The email address of the user
 * @property enabled Whether the user account is enabled
 * @property verificationCode The users verification code, if they are not yet verified
 */
data class User(
        val identity: Identity<UserId>?,
        val name: String,
        val email: String?,
        val enabled: Boolean,
        val verificationCode: String?
)