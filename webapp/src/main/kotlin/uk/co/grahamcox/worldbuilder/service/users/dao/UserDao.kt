package uk.co.grahamcox.worldbuilder.service.users.dao

import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId

/**
 * Mechanism to access User records in the database
 */
interface UserDao {
    /**
     * Get a single User record by ID
     * @param id The ID of the User
     * @return the User record
     * @throws ResourceNotFoundException if the user was not found
     */
    fun getById(id: UserId) : User

    /**
     * Save the given user
     * @param user The user to save.
     * If the user has an Identity then it will update this user in the data store.
     * If the user does not have an Identity then it will create a new user in the data store
     * @return the user as it has been saved
     * @throws ResourceNotFoundException if the user was not found
     * @throws StaleResourceException if the user was stale
     */
    fun save(user: User) : User

    /**
     * Delete the given user
     * @param user The user to delete
     * @throws ResourceNotFoundException if the user was not found
     * @throws StaleResourceException if the user was stale
     */
    fun delete(user: Identity<UserId>)
}