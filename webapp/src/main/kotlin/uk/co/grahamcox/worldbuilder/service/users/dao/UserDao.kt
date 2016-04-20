package uk.co.grahamcox.worldbuilder.service.users.dao

import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId

/**
 * Mechanism to load and save user details to the data store
 */
interface UserDao {
    /**
     * Get the user that has the given ID
     * @param id The ID of the user
     * @return the user with the given ID
     * @throws uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException if the user with the given ID doesn't exist
     */
    fun getById(id: UserId) : User

    /**
     * List all of the known users
     * @return the list of all known users
     */
    fun list() : List<User>

    /**
     * Delete the given user
     * @param user The user to delete
     */
    fun delete(user: User)

    /**
     * Save the given user
     * @param user The user details to save. If the user has an ID then it will update what is in the data store. If it
     * does not have an ID then a new user will be created.
     * @return The newly saved user details
     */
    fun save(user: User) : User
}