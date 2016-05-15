package uk.co.grahamcox.worldbuilder.verification.users

/**
 * Model representation of a user to create
 * @property name The name of the user
 * @property email The email address of the user
 */
data class NewUserModel(var name: String? = null,
                        var email: String? = null)