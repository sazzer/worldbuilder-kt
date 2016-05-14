package uk.co.grahamcox.worldbuilder.verification.users

import uk.co.grahamcox.worldbuilder.verification.ErrorsResponse
import uk.co.grahamcox.worldbuilder.verification.Result

/**
 * Facade layer for working with users
 * @property userCreator The mechanism by which to actually create a user
 */
class UserFacade(private val userCreator: UserCreator) {

    /** Result of creating a user */
    var createdUserDetails: Result<UserDetailsResponse, ErrorsResponse>? = null
        private set

    /**
     * Attempt to create a user with the given details
     * @param details The details of the user to create
     */
    fun createUser(details: NewUserModel) {
        createdUserDetails = userCreator.createUser(details)
    }
}