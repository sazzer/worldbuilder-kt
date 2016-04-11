package uk.co.grahamcox.worldbuilder.service.users

/**
 * Representation of an actual User
 * @property id The ID of the User
 * @property name The name of the User
 */
data class User(val id: UserId,
                val name: String)